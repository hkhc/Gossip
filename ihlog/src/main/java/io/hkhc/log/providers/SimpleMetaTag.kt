package io.hkhc.log.providers

import io.hkhc.log.MetaTag

class SimpleMetaTag(private val tag: String) : MetaTag {
    override fun getTag() = tag
}
