package io.hkhc.log.providers

import android.content.Context
import io.hkhc.log.MetaTag
import io.hkhc.log.internal.TagMaker

class AndroidPackageMetaTag(private val context: Context) : MetaTag {
    override fun getTag() = TagMaker.getPackageNameAbbr(context.packageName)
}
