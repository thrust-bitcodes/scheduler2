const LocalDateTime = Java.type('java.time.LocalDateTime')

const resource = {
    count: 2
}

exports = (task) => {
	const now = LocalDateTime.now()
	console.log(`[Task 03]|Count=${resource.count}|Time=${now}|`)
	--resource.count
	if (!resource.count) {
		console.log('[Task 03]|Task canceled')
		task.cancel()
	}
}