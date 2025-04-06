document.addEventListener("DOMContentLoaded", () => {
    document.getElementById("loginForm").addEventListener("submit", (e) => {
        e.preventDefault();

        const username = document.getElementById("username").value;
        const password = document.getElementById("password").value;

        const adminCredentials = { username: "mahmoud", password: "123" };

        if (username === adminCredentials.username && password === adminCredentials.password) {
            localStorage.setItem("loggedInUser", JSON.stringify({ username }));

            window.location.href = "admin.html";
        } else {
            alert("Invalid username or password! Try again.");
        }
    });
});