package com.alexpi.awesometanks.game.systems

import com.artemis.BaseEntitySystem

abstract class SortedIteratingSystem: BaseEntitySystem() {
    private var shouldSort = false

    fun forceSort() {
        shouldSort = true
    }

    override fun removed(entityId: Int) {
        super.removed(entityId)
        shouldSort = true
    }

    override fun inserted(entityId: Int) {
        super.inserted(entityId)
        shouldSort = true
    }

    private fun sort(ids: IntArray): IntArray {
        return if (shouldSort) {
            shouldSort = false
            ids.sortedBy { comparator(it) }.toIntArray()
        } else ids
    }

    protected abstract fun comparator(entityId: Int): Int

    protected abstract fun process(entityId: Int)

    override fun processSystem() {
        val actives = subscription.entities
        val ids = sort(actives.data)

        ids.forEach { id ->
            process(id)
        }

    }
}