package xyz.haff.quoteapi.data

import org.bson.Document
import org.springframework.data.mongodb.core.aggregation.AggregationOperation
import org.springframework.data.mongodb.core.aggregation.AggregationOperationContext

class NativeAggregationOperation(
    private val operationAsString: String,
) : AggregationOperation {
    override fun toDocument(context: AggregationOperationContext): Document? {
        return null
    }

    override fun toPipelineStages(context: AggregationOperationContext): MutableList<Document>
        = mutableListOf(context.getMappedObject(Document.parse(operationAsString)))

    override fun getOperator(): String = "\$lookup"
}