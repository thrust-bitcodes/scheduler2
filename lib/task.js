const Thread = Java.type('java.lang.Thread')

const runScheduleTask = (script, schedulerTask) => {
    let task = require(script);
    if (typeof task !== 'function') {
        if (typeof task.task !== 'function') {
            throw new Error('Didn\'t find task function in :' + script)
        } else {
            task = task.task
        }
    }
    task(schedulerTask)
}

exports = {
	runScheduleTask
}