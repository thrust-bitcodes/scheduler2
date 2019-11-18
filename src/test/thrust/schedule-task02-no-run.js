const Thread = Java.type('java.lang.Thread')
const scheduler = require('scheduler2')

scheduler.schedule( `${__ROOT_DIR__}/task02.js`, 1000, false,)
console.log('schedule-task02: wait')
Thread.sleep(600)
console.log('schedule-task02: cancel')
scheduler.cancel()
console.log('schedule-task02: end')
