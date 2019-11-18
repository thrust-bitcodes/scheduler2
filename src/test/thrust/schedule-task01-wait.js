const scheduler = require('scheduler2')

scheduler.schedule(`${__ROOT_DIR__}/task01.js`, 5000, false)
scheduler.wait(6000)