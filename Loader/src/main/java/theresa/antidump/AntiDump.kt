package theresa.antidump

import java.lang.management.ManagementFactory

interface AntiDump {
    /**
     * Checks the JVM launch arguments for flags used to:
     * - Attach a java agent
     * - Debug aspects such as classloading
     * - Add networking proxies
     * - Compromise ssl security
     */
    fun checkLaunchFlags()

    /**
     * Compromises classes required for java agents
     */
    fun disableJavaAgents()

    /**
     * Sets a package name filter, this tells some debugging tools that dump classes to exclude classes within certain
     * packages
     */
    fun setPackageNameFilter()

    /**
     * Nullifies the structure info exported by the JVM dll
     *
     * This prevents debug tools from knowing the addresses of different exported functions, structs etc
     */
    fun dissasembleStructs()

    /**
     * Shutsdown the JVM without any Java level stacktraces/hooks etc
     */
    fun shutdownHard(): Nothing


    companion object : AntiDump {
        private val naughtyFlags = arrayOf("-javaagent", "-Xdebug", "-agentlib", "-Xrunjdwp", "-Xnoagent", "-verbose", "-DproxySet", "-DproxyHost", "-DproxyPort", "-Djavax.net.ssl.trustStore", "-Djavax.net.ssl.trustStorePassword")

        override fun checkLaunchFlags() {
            ManagementFactory.getRuntimeMXBean().inputArguments.firstOrNull {
                naughtyFlags.contains(it)
            }?.let {
                shutdownHard()
            }
        }

        override fun disableJavaAgents() {
            try {
                val bytes = createDummyClass("sun/instrument/InstrumentationImpl")
                unsafe.defineClass("sun.instrument.InstrumentationImpl", bytes, 0, bytes.size, null, null)
            } catch (e: Throwable) {
                e.printStackTrace()
                shutdownHard()
            }
        }

        override fun setPackageNameFilter() {
            val bytes = createDummyClass("theresa/antidump/MaliciousClassFilter")
            unsafe.defineClass("theresa.antidump.MaliciousClassFilter", bytes, 0, bytes.size, null, null)
            System.setProperty("sun.jvm.hotspot.tools.jcore.filter", "theresa.antidump.MaliciousClassFilter")
        }

        override fun dissasembleStructs() {
            StructDissasembler.disassembleStruct()
        }

        override inline fun shutdownHard(): Nothing {
            try {
                // This causes a JVM segfault without a java stacktrace
                unsafe.putAddress(0, 0)
            } catch (ignored: Exception) {
            }
            Runtime.getRuntime().exit(0)
            throw Error().also { it.stackTrace = arrayOf() }
        }
    }
}