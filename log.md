获取记录成功: {code: 200, message: '操作成功', data: Array(5)}
index.vue:1270 原始记录数据: [{"record_id":12,"amount":20,"is_deleted":0,"month":4,"updated_at":"2025-04-02 07:45:45","user_id":2,"year":2025,"description":"交通","created_at":"2025-04-02 07:45:45","category":"transport","record_time":"2025-04-11 15:45:45","day":11},{"record_id":11,"amount":200,"is_deleted":0,"month":4,"updated_at":"2025-04-02 07:20:46","user_id":2,"year":2025,"description":"教育","created_at":"2025-04-02 07:20:46","category":"education","record_time":"2025-04-03 15:20:46","day":3},{"record_id":8,"amount":15,"is_deleted":0,"month":4,"updated_at":"2025-04-02 06:55:17","user_id":2,"year":2025,"description":"餐饮","created_at":"2025-04-02 06:55:17","category":"food","record_time":"2025-04-02 14:55:17","day":2},{"record_id":6,"amount":15,"is_deleted":0,"month":4,"updated_at":"2025-04-02 04:03:30","user_id":2,"year":2025,"description":"餐饮","created_at":"2025-04-02 04:03:30","category":"food","record_time":"2025-04-02 12:03:29","day":2},{"record_id":5,"amount":15,"is_deleted":0,"month":4,"updated_at":"2025-04-01 17:50:06","user_id":2,"year":2025,"description":"餐饮","created_at":"2025-04-01 17:50:06","category":"food","record_time":"2025-04-01 01:50:06","day":1}]
index.vue:1288 分类代码映射: {food: 1, transport: 2, shopping: 3, entertainment: 4, housing: 5, …}
index.vue:1302 处理第1条记录: {"record_id":12,"amount":20,"is_deleted":0,"month":4,"updated_at":"2025-04-02 07:45:45","user_id":2,"year":2025,"description":"交通","created_at":"2025-04-02 07:45:45","category":"transport","record_time":"2025-04-11 15:45:45","day":11}
index.vue:1319 记录的category值: transport
index.vue:1348 解析后的分类名称: 交通, 分类ID: 2
index.vue:1366 格式化后的记录: {id: 12, category: '交通', categoryValue: 2, icon: '🚇', amount: 20, …}
index.vue:1377 添加到其他组
index.vue:1302 处理第2条记录: {"record_id":11,"amount":200,"is_deleted":0,"month":4,"updated_at":"2025-04-02 07:20:46","user_id":2,"year":2025,"description":"教育","created_at":"2025-04-02 07:20:46","category":"education","record_time":"2025-04-03 15:20:46","day":3}
index.vue:1319 记录的category值: education
index.vue:1348 解析后的分类名称: 教育, 分类ID: 7
index.vue:1366 格式化后的记录: {id: 11, category: '教育', categoryValue: 7, icon: '📚', amount: 200, …}
index.vue:1377 添加到其他组
index.vue:1302 处理第3条记录: {"record_id":8,"amount":15,"is_deleted":0,"month":4,"updated_at":"2025-04-02 06:55:17","user_id":2,"year":2025,"description":"餐饮","created_at":"2025-04-02 06:55:17","category":"food","record_time":"2025-04-02 14:55:17","day":2}
index.vue:1319 记录的category值: food
index.vue:1348 解析后的分类名称: 餐饮, 分类ID: 1
index.vue:1366 格式化后的记录: {id: 8, category: '餐饮', categoryValue: 1, icon: '🍜', amount: 15, …}
index.vue:1371 添加到今天组
index.vue:1302 处理第4条记录: {"record_id":6,"amount":15,"is_deleted":0,"month":4,"updated_at":"2025-04-02 04:03:30","user_id":2,"year":2025,"description":"餐饮","created_at":"2025-04-02 04:03:30","category":"food","record_time":"2025-04-02 12:03:29","day":2}
index.vue:1319 记录的category值: food
index.vue:1348 解析后的分类名称: 餐饮, 分类ID: 1
index.vue:1366 格式化后的记录: {id: 6, category: '餐饮', categoryValue: 1, icon: '🍜', amount: 15, …}
index.vue:1371 添加到今天组
index.vue:1302 处理第5条记录: {"record_id":5,"amount":15,"is_deleted":0,"month":4,"updated_at":"2025-04-01 17:50:06","user_id":2,"year":2025,"description":"餐饮","created_at":"2025-04-01 17:50:06","category":"food","record_time":"2025-04-01 01:50:06","day":1}
index.vue:1319 记录的category值: food
index.vue:1348 解析后的分类名称: 餐饮, 分类ID: 1
index.vue:1366 格式化后的记录: {id: 5, category: '餐饮', categoryValue: 1, icon: '🍜', amount: 15, …}
index.vue:1374 添加到昨天组
index.vue:1390 处理后的所有记录数据: {today: Array(2), yesterday: Array(1), other: 2}
index.vue:541 获取记录成功: {code: 200, message: '操作成功', data: Array(5)}code: 200data: (5) [{…}, {…}, {…}, {…}, {…}]message: "操作成功"[[Prototype]]: Object
index.vue:1270 原始记录数据: [{"record_id":12,"amount":20,"is_deleted":0,"month":4,"updated_at":"2025-04-02 07:45:45","user_id":2,"year":2025,"description":"交通","created_at":"2025-04-02 07:45:45","category":"transport","record_time":"2025-04-11 15:45:45","day":11},{"record_id":11,"amount":200,"is_deleted":0,"month":4,"updated_at":"2025-04-02 07:20:46","user_id":2,"year":2025,"description":"教育","created_at":"2025-04-02 07:20:46","category":"education","record_time":"2025-04-03 15:20:46","day":3},{"record_id":8,"amount":15,"is_deleted":0,"month":4,"updated_at":"2025-04-02 06:55:17","user_id":2,"year":2025,"description":"餐饮","created_at":"2025-04-02 06:55:17","category":"food","record_time":"2025-04-02 14:55:17","day":2},{"record_id":6,"amount":15,"is_deleted":0,"month":4,"updated_at":"2025-04-02 04:03:30","user_id":2,"year":2025,"description":"餐饮","created_at":"2025-04-02 04:03:30","category":"food","record_time":"2025-04-02 12:03:29","day":2},{"record_id":5,"amount":15,"is_deleted":0,"month":4,"updated_at":"2025-04-01 17:50:06","user_id":2,"year":2025,"description":"餐饮","created_at":"2025-04-01 17:50:06","category":"food","record_time":"2025-04-01 01:50:06","day":1}]
index.vue:1288 分类代码映射: {food: 1, transport: 2, shopping: 3, entertainment: 4, housing: 5, …}
index.vue:1302 处理第1条记录: {"record_id":12,"amount":20,"is_deleted":0,"month":4,"updated_at":"2025-04-02 07:45:45","user_id":2,"year":2025,"description":"交通","created_at":"2025-04-02 07:45:45","category":"transport","record_time":"2025-04-11 15:45:45","day":11}
index.vue:1319 记录的category值: transport
index.vue:1348 解析后的分类名称: 交通, 分类ID: 2
index.vue:1366 格式化后的记录: {id: 12, category: '交通', categoryValue: 2, icon: '🚇', amount: 20, …}
index.vue:1377 添加到其他组
index.vue:1302 处理第2条记录: {"record_id":11,"amount":200,"is_deleted":0,"month":4,"updated_at":"2025-04-02 07:20:46","user_id":2,"year":2025,"description":"教育","created_at":"2025-04-02 07:20:46","category":"education","record_time":"2025-04-03 15:20:46","day":3}
index.vue:1319 记录的category值: education
index.vue:1348 解析后的分类名称: 教育, 分类ID: 7
index.vue:1366 格式化后的记录: {id: 11, category: '教育', categoryValue: 7, icon: '📚', amount: 200, …}
index.vue:1377 添加到其他组
index.vue:1302 处理第3条记录: {"record_id":8,"amount":15,"is_deleted":0,"month":4,"updated_at":"2025-04-02 06:55:17","user_id":2,"year":2025,"description":"餐饮","created_at":"2025-04-02 06:55:17","category":"food","record_time":"2025-04-02 14:55:17","day":2}
index.vue:1319 记录的category值: food
index.vue:1348 解析后的分类名称: 餐饮, 分类ID: 1
index.vue:1366 格式化后的记录: {id: 8, category: '餐饮', categoryValue: 1, icon: '🍜', amount: 15, …}
index.vue:1371 添加到今天组
index.vue:1302 处理第4条记录: {"record_id":6,"amount":15,"is_deleted":0,"month":4,"updated_at":"2025-04-02 04:03:30","user_id":2,"year":2025,"description":"餐饮","created_at":"2025-04-02 04:03:30","category":"food","record_time":"2025-04-02 12:03:29","day":2}
index.vue:1319 记录的category值: food
index.vue:1348 解析后的分类名称: 餐饮, 分类ID: 1
index.vue:1366 格式化后的记录: {id: 6, category: '餐饮', categoryValue: 1, icon: '🍜', amount: 15, …}
index.vue:1371 添加到今天组
index.vue:1302 处理第5条记录: {"record_id":5,"amount":15,"is_deleted":0,"month":4,"updated_at":"2025-04-01 17:50:06","user_id":2,"year":2025,"description":"餐饮","created_at":"2025-04-01 17:50:06","category":"food","record_time":"2025-04-01 01:50:06","day":1}
index.vue:1319 记录的category值: food
index.vue:1348 解析后的分类名称: 餐饮, 分类ID: 1
index.vue:1366 格式化后的记录: {id: 5, category: '餐饮', categoryValue: 1, icon: '🍜', amount: 15, …}
index.vue:1374 添加到昨天组
index.vue:1390 处理后的所有记录数据: {today: Array(2), yesterday: Array(1), other: 2}other: 2today: Array(2)0: {id: 8, category: '餐饮', categoryValue: 1}1: {id: 6, category: '餐饮', categoryValue: 1}category: "餐饮"categoryValue: 1id: 6[[Prototype]]: Objectlength: 2[[Prototype]]: Array(0)at: ƒ at()concat: ƒ concat()constructor: ƒ Array()copyWithin: ƒ copyWithin()entries: ƒ entries()every: ƒ every()fill: ƒ fill()filter: ƒ filter()find: ƒ find()findIndex: ƒ findIndex()findLast: ƒ findLast()findLastIndex: ƒ findLastIndex()flat: ƒ flat()flatMap: ƒ flatMap()forEach: ƒ forEach()includes: ƒ includes()indexOf: ƒ indexOf()join: ƒ join()keys: ƒ keys()lastIndexOf: ƒ lastIndexOf()length: 0map: ƒ map()pop: ƒ pop()push: ƒ push()reduce: ƒ reduce()reduceRight: ƒ reduceRight()reverse: ƒ reverse()shift: ƒ shift()slice: ƒ slice()some: ƒ some()sort: ƒ sort()splice: ƒ splice()toLocaleString: ƒ toLocaleString()toReversed: ƒ toReversed()toSorted: ƒ toSorted()toSpliced: ƒ toSpliced()toString: ƒ toString()unshift: ƒ unshift()values: ƒ values()with: ƒ with()Symbol(Symbol.iterator): ƒ values()Symbol(Symbol.unscopables): {at: true, copyWithin: true, entries: true, fill: true, find: true, …}[[Prototype]]: Objectyesterday: Array(1)0: {id: 5, category: '餐饮', categoryValue: 1}length: 1[[Prototype]]: Array(0)[[Prototype]]: Object
index.vue:647 当前筛选条件: Proxy(Object) {label: '全部类型', value: 'all'}[[Handler]]: MutableReactiveHandler_isReadonly: false_isShallow: false[[Prototype]]: BaseReactiveHandler[[Target]]: Object[[IsRevoked]]: false
index.vue:648 所有记录数据: Proxy(Object) {today: Array(2), yesterday: Array(1), other: Array(2)}[[Handler]]: MutableReactiveHandler_isReadonly: false_isShallow: false[[Prototype]]: BaseReactiveHandler[[Target]]: Object[[IsRevoked]]: false
index.vue:652 选择了全部类型，返回所有记录