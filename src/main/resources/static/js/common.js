document.addEventListener("click", function (e) {
    if (e.target.classList.contains("apply-button")) {
        const id = e.target.dataset.id;
        fetch(`/raffle/${id}/apply`, {method: "POST"}).then(response => {
            if (!response.ok) {
                throw new Error("Request failed");
            }
            location.reload();
        });
    }
});