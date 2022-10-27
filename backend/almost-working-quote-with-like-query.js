[{
 $match: {
  _id: ObjectId('6352bf48668a077fda0fd6f5')
 }
}, {
 $lookup: {
  from: 'users',
  'let': {
   quote_id: '$_id'
  },
  pipeline: [
   {
    $match: {
     _id: ObjectId('63594c6292e52a5f9156392a')
    }
   },
   {
    $project: {
     quote_id: '$$quote_id',
     liked: {
      $in: [
       '$$quote_id',
       '$liked_quotes'
      ]
     }
    }
   }
  ],
  as: 'intermediate_result'
 }
}, {
 $project: {
  _id: 1,
  text: 1,
  author: 1,
  work: 1,
  tags: 1,
  liked: {
   $getField: {
    field: 'liked',
    input: {
     $first: '$intermediate_result'
    }
   }
  }
 }
}]
