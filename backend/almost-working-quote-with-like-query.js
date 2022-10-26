// This query almost works to retrieve a quote and wether a given user liked it... but returns the result as an array, with a single object, with an _id field with the value

[{
 $match: {
  _id: «quote id»
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
     _id: «user id»
    }
   },
   {
    $project: {
     _id: {
      $in: [
       '$$quote_id',
       '$liked_quotes'
      ]
     }
    }
   }
  ],
  as: 'liked'
 }
}]
