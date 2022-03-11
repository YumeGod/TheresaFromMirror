package theresa.antidump

import sun.misc.Unsafe


val unsafe: Unsafe by lazy {
    Unsafe::class.java.getDeclaredField("theUnsafe").let {
        it.isAccessible = true
        it[null] as Unsafe
    }
}