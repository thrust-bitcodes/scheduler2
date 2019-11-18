const scheduler = require('scheduler2')

scheduler.schedule(`${__ROOT_DIR__}/task02.js`, 1000, true)
scheduler.wait(6001)
