package com.se.fintechadvise.ManagerClasses

import com.se.fintechadvise.DataClasses.Article

object ArticleManager {
    var selectedArticle : Article? = null

    fun setCurrentArticle(article: Article) {
        selectedArticle = article
    }
    fun getCurrentArticle(): Article?{
        return selectedArticle
    }

    fun clearCurrentArticle() {
        selectedArticle = null
    }
}