package xyz.haff.quoteapi.data.repository

suspend fun QuoteRepository.chooseRandom(author: String? = null, tags: List<String>? = null) = when { // Not very comfortable...
    author == null && tags == null -> getRandomId()
    author != null && tags == null -> getRandomIdByAuthor(author)
    author == null && tags != null -> getRandomIdByTags(tags)
    author != null && tags != null -> getRandomIdByAuthorAndTags(author, tags)
    else -> throw IllegalArgumentException("Combination of parameters to `chooseRandom` not supported")
}