document.addEventListener("DOMContentLoaded", () => {
    const callLogsTable = document.getElementById("callLogsTable").getElementsByTagName("tbody")[0];
    let callLogs = [];

    // Fetch call logs from the server
    async function fetchCallLogs() {
        try {
            const response = await fetch('/service/CallLogs');
            const text = await response.text();

            try {
                callLogs = JSON.parse(text);
                renderCallLogs();
            } catch (err) {
                console.error('Invalid JSON:', text);
                throw err;
            }
        } catch (error) {
            console.error('Error fetching call logs:', error);
            alert('Failed to fetch call logs. Please try again.');
        }
    }

    // Render call logs in the table
    function renderCallLogs() {
        callLogsTable.innerHTML = "";
        callLogs.forEach(log => {
            const row = document.createElement("tr");
            
            // Calculate duration in minutes and seconds
            const startTime = new Date(log.startTime);
            const endTime = new Date(log.endTime);
            const durationMs = endTime - startTime;
            const durationMinutes = Math.floor(durationMs / 60000);
            const durationSeconds = Math.floor((durationMs % 60000) / 1000);
            const duration = `${durationMinutes}m ${durationSeconds}s`;

            row.innerHTML = `
                <td>${log.id}</td>
                <td>${log.caller}</td>
                <td>${log.callee}</td>
                <td>${formatDateTime(startTime)}</td>
                <td>${formatDateTime(endTime)}</td>
                <td>${duration}</td>
            `;
            callLogsTable.appendChild(row);
        });
    }

    // Format date and time
    function formatDateTime(date) {
        return date.toLocaleString('en-US', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit',
            second: '2-digit',
            hour12: false
        });
    }

    // Initialize the page
    fetchCallLogs();

    // Refresh call logs every 30 seconds
    setInterval(fetchCallLogs, 30000);
}); 