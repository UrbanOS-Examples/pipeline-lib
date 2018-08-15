package org.scos.pipeline

class ReleaseNumber {
    static String candidate() { "RC-${new Date().format("yyyy.MM.dd.HHmmss")}" }
}