package com.paulchibamba.margin.data.cover

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import com.paulchibamba.margin.domain.model.BookSlug
import com.paulchibamba.margin.domain.model.CoverSource
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode
import java.io.ByteArrayOutputStream
import java.io.File
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class FileCoverImageStoreTest {

    @get:Rule
    val folder = TemporaryFolder()

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val book = BookSlug("alice-bob-appsec")
    private val at = Instant.ofEpochMilli(6_000)
    private val root by lazy { File(folder.root, CoverDirectory.NAME) }

    @Test
    fun `saving scales the long edge down to 600 px and writes WebP`() = runTest {
        val path = storeFor(this).save(book, pickedImage(width = 1200, height = 1800), at)

        val saved = File(checkNotNull(path))
        assertEquals(File(root, "alice-bob-appsec-6000.webp"), saved)
        assertTrue(saved.isWebp())
        assertEquals(400 to 600, saved.dimensions())
    }

    @Test
    fun `a small image keeps its size`() = runTest {
        val path = storeFor(this).save(book, pickedImage(width = 300, height = 200), at)

        assertEquals(300 to 200, File(checkNotNull(path)).dimensions())
    }

    @Test
    fun `a web image is downloaded, scaled and written as WebP`() = runTest {
        val png = pngBytes(width = 900, height = 1200)
        val downloader = CoverDownloader(open = { url -> FakeConnection(url, png) })
        val store = storeFor(this, downloader)

        val path = store.save(book, CoverSource.WebImage("https://covers.example/appsec.png"), at)

        val saved = File(checkNotNull(path))
        assertTrue(saved.isWebp())
        assertEquals(450 to 600, saved.dimensions())
    }

    @Test
    fun `a refused web image saves nothing`() = runTest {
        val path = storeFor(this).save(book, CoverSource.WebImage("http://covers.example/appsec.png"), at)

        assertNull(path)
        assertTrue(root.listFiles().orEmpty().isEmpty())
    }

    @Test
    fun `an unreadable image saves nothing`() = runTest {
        val notAnImage = folder.newFile("notes.txt").apply { writeText("not an image") }

        val path = storeFor(this).save(book, CoverSource.GalleryImage(Uri.fromFile(notAnImage).toString()), at)

        assertNull(path)
        assertTrue(root.listFiles().orEmpty().isEmpty())
    }

    @Test
    fun `deleting removes a cover but never a file outside the covers folder`() = runTest {
        val store = storeFor(this)
        val saved = File(checkNotNull(store.save(book, pickedImage(width = 300, height = 200), at)))
        val outside = folder.newFile("margin.db")

        store.delete(saved.path)
        store.delete(outside.path)
        store.delete(File(root, "../margin.db").path)

        assertFalse(saved.exists())
        assertTrue(outside.exists())
    }

    private fun storeFor(scope: TestScope, downloader: CoverDownloader = CoverDownloader()) = FileCoverImageStore(
        context.contentResolver,
        CoverDirectory(root),
        downloader = downloader,
        dispatcher = StandardTestDispatcher(scope.testScheduler),
    )

    private fun pickedImage(width: Int, height: Int): CoverSource {
        val file = folder.newFile("picked-${width}x$height.png").apply { writeBytes(pngBytes(width, height)) }
        return CoverSource.GalleryImage(Uri.fromFile(file).toString())
    }

    private fun pngBytes(width: Int, height: Int): ByteArray {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply { eraseColor(Color.RED) }
        return ByteArrayOutputStream().also { bytes -> bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes) }
            .toByteArray()
    }

    private fun File.isWebp(): Boolean {
        val header = readBytes().take(12).toByteArray()
        return String(header, 0, 4) == "RIFF" && String(header, 8, 4) == "WEBP"
    }

    private fun File.dimensions(): Pair<Int, Int> {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(path, bounds)
        return bounds.outWidth to bounds.outHeight
    }
}
