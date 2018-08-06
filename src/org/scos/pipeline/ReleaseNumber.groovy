package org.scos.pipeline

class ReleaseNumber {
    static String release() { "${new Date().format("yyyy.MM.dd.HHmmss")}" }
    static String candidate() { "RC-${release()}" }
}