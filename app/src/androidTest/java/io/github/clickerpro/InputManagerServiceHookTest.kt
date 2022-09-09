package io.github.clickerpro

import android.inputmethodservice.InputMethodService
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.clickerpro.core.TestApplication
import org.junit.BeforeClass
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class InputManagerServiceHookTest {

    companion object {
        private lateinit var ims: InputMethodService

        @BeforeClass
        fun getInputManagerService() {
            ims = TestApplication.CONTEXT.getSystemService(InputMethodService::class.java)
        }
    }
}