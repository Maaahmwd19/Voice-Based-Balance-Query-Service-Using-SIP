document.getElementById("logoutBtn").addEventListener("click", function () {
    localStorage.removeItem("loggedInUser");
    localStorage.removeItem("role");
    window.location.href = "login.html"; 
});
