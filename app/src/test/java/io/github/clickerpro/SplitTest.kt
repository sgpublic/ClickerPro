package io.github.clickerpro

import org.junit.Test
import java.util.regex.Pattern

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class SplitTest {
    @Test
    fun split_isCorrect() {
        val s = "EV_KEY       0152                 DOWN"
        println(s.split(Pattern.compile("\\s+")))
    }
}