const resource = {
    count: 5
}
exports = (task) => {
	console.log(`Task 02 - Count: ${resource.count}`)
	--resource.count
	if (!resource.count) {
		task.cancel()
	}
}