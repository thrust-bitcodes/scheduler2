const scheduler = require('scheduler2')
const LocalDateTime = Java.type('java.time.LocalDateTime')

const fillLeftZeros = (str) => `${str}`.length >= 2 
	? '' + str
	: fillLeftZeros('0' + str)

const localDateTimeToString = (date) => {
	const h = fillLeftZeros(date.getHour())
	const m = fillLeftZeros(date.getMinute())
	const s = fillLeftZeros(date.getSecond())
	
	return h + ':' + m + ':' + s
} 
const now = LocalDateTime.now()
const after40Seconds = now.plusSeconds(30)
const after80Seconds = now.plusSeconds(60)

const times = [localDateTimeToString(after40Seconds), localDateTimeToString(after80Seconds)]

const timesStr = times.reduce((str, t) => str + '|' + t, '')

console.log('[Scheduling]>>' + timesStr)
scheduler.schedule(`${__ROOT_DIR__}/task03.js`, times , true)
scheduler.wait(30000)
console.log('[Scheduling]>>Finished')
