package io.hkhc.log.providers

import io.hkhc.log.IHLog
import io.hkhc.log.Priority
import io.hkhc.log.internal.LogFactory

class PriorityLog(val delegate: IHLog): IHLog {

    override fun exception(tag: String?, message: String?, throwable: Throwable) {
        if (!Priority.Error.shouldBeFilteredBy(LogFactory.logLevel)) {
            delegate.exception(tag, message, throwable)
        }
    }

    override fun log(priority: Priority, tag: String?, message: String) {
        if (!priority.shouldBeFilteredBy(LogFactory.logLevel)) {
            delegate.log(priority, tag, message)
        }
    }

    override fun getLogTag() = delegate.getLogTag()
}
