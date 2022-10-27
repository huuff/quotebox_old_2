package xyz.haff.quoteapi.data

// TODO: Absolute wizardry. Put it into a library.
object MongoOperators {
    const val match = "\$match"
    const val lookup = "\$lookup"
    const val sample = "\$sample"
    const val all = "\$all"
    const val project = "\$project"
    const val `in` = "\$in"
    const val getField = "\$getField"
    const val first = "\$first"
}