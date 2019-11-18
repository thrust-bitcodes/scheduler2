const Scheduler = Java.type('br.com.softbox.thrust.scheduler.Scheduler')
const File = Java.type('java.io.File')

const schedulerManager = {}

const initScheduler = (minThreads, maxThreads) => {
	minThreads = minThreads || 4
	maxThreads = maxThreads || minThreads
	schedulerManager.scheduler = Scheduler.initScheduler(minThreads, maxThreads, __ROOT_DIR__)
}
const getScheduler = () => {
	if (!schedulerManager.scheduler) {
		initScheduler()
	}
	return schedulerManager.scheduler
}
const validateParams = (taskScript, time) => {
	if (!taskScript) {
		throw new Error('No script was informed')
	}
	const scriptFile = new File(taskScript)
	if (!scriptFile.exists()) {
		throw new Error('Script not found: ' + taskScript)
	}
	if (!time) {
		throw new Error('No time was informed')
	}
	
	const typeTime = typeof time
	
	if (Array.isArray(time)) {
		const validateForReduce = (err, hour, index) => {
			if (!err) {
				if (!hour) {
					err = 'No value for time at index ' + index
				} else if (typeof hour !== 'string') {
					err = 'Expected time as string at index ' + index + ': ' + hour
				}  
			}
			return err
		}
		const error = time.reduce(validateForReduce, null)
	} else if (!typeTime === 'string' && !typeTime === 'number') {
		throw new Error('Invalid time type: ' + time)
	}
}
const schedule = (taskScript, time, now) => {
	now =  !!now
	validateParams(taskScript, time)
	if (Array.isArray(time)) {
		getScheduler().timeSchedule(taskScript, time, now)
	} else if (typeof time === 'string') {
		getScheduler().timeSchedule(taskScript, [time], now)
	} else {
		getScheduler().simpleSchedule(taskScript, time, now)
	}
}
const wait = (time) => getScheduler().waitSchedulers(time)
const cancel = () => getScheduler().cancel()

exports = {
	initScheduler,
	schedule,
	wait,
	cancel
}