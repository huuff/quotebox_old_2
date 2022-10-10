package xyz.haff.quoteapi.data.repository

fun QuoteRepository.chooseRandom(author: String? = null, tags: List<String>? = null) = when { // Not very comfortable...
    author == null && tags == null -> getRandom()
    author != null && tags == null -> getRandomByAuthor(author)
    author == null && tags != null -> getRandomByTags(tags)
    author != null && tags != null -> getRandomByAuthorAndTags(author, tags)
    else -> throw IllegalArgumentException("Combination of parameters to `chooseRandom` not supported")
}