document.addEventListener("click", function (e) {
    if (e.target.classList.contains("apply-button")) {
        const id = e.target.dataset.id;
        const token = document.querySelector('meta[name="_csrf"]').content;
        const header = document.querySelector('meta[name="_csrf_header"]').content;
        fetch(`/raffle/${id}/apply`, {
            method: "POST",
            headers: {
                [header]: token
            }
        }).then(response => {
            if (!response.ok) {
                throw new Error("Request failed");
            }
            location.reload();
        });
    } else if (e.target.classList.contains("close-button")) {
        const id = e.target.dataset.id;
        const token = document.querySelector('meta[name="_csrf"]').content;
        const header = document.querySelector('meta[name="_csrf_header"]').content;
        fetch(`/raffle/${id}/close`, {
            method: "POST",
            headers: {
                [header]: token
            }
        }).then(response => {
            if (!response.ok) {
                throw new Error("Request failed");
            }
            location.reload();
        });
    }
});