document.addEventListener("DOMContentLoaded", function () {

    // =============================
    // Popup Message (Error)
    // =============================
    const popupMessage = document.getElementById("popupMessage");
    const btnClosePopup = document.getElementById("closePopup");

    if (popupMessage && btnClosePopup) {
        btnClosePopup.addEventListener("click", function () {
            popupMessage.classList.add("opacity-0");
            setTimeout(() => popupMessage.remove(), 300);
        });
    }

    // =============================
    // Popup Deleted (Success)
    // =============================
    const popupDeleted = document.getElementById('popupDeleted');
    const deletedOk = document.getElementById('deletedOk');
    const deleted = /*[[${deleted}]]*/ false;

    if (deleted && popupDeleted) {
        popupDeleted.classList.remove('hidden');
    }

    if (deletedOk && popupDeleted) {
        deletedOk.addEventListener('click', () => {
            popupDeleted.classList.add('hidden');
        });

        popupDeleted.addEventListener('click', (e) => {
            if (e.target === popupDeleted) {
                popupDeleted.classList.add('hidden');
            }
        });
    }

    // =============================
    // Tabs Logic
    // =============================
    const tabLinks = document.querySelectorAll(".tab-link");
    const tabContents = document.querySelectorAll(".tab-content");
    const container = document.querySelector(".tab-container");

    function setContainerHeight(tab) {
        if (container && tab) {
            container.style.height = tab.offsetHeight + "px";
        }
    }

    const activeTab = document.querySelector(".tab-content.active");
    if (activeTab) {
        setContainerHeight(activeTab);
    }

    tabLinks.forEach(link => {
        link.addEventListener("click", (e) => {
            e.preventDefault();

            const targetTabId = link.getAttribute("data-tab");
            const targetTab = document.getElementById(targetTabId);
            if (!targetTab) return;

            tabLinks.forEach(l => {
                l.classList.remove("border-primary", "text-primary", "font-bold");
                l.classList.add("border-transparent", "text-slate-500", "dark:text-slate-400", "font-semibold");
            });

            link.classList.remove("border-transparent", "text-slate-500", "dark:text-slate-400", "font-semibold");
            link.classList.add("border-primary", "text-primary", "font-bold");

            tabContents.forEach(tab => tab.classList.remove("active"));
            targetTab.classList.add("active");

            setContainerHeight(targetTab);
        });
    });

    // =============================
    // Rating Stars
    // =============================
    const ratingContainer = document.querySelector(".review-stars");
    const ratingInput = document.getElementById("ratingInput");

    if (ratingContainer && ratingInput) {
        const stars = ratingContainer.querySelectorAll(".material-symbols-outlined");
        let rating = 0;

        stars.forEach((star, index) => {
            star.addEventListener("mouseenter", () => {
                highlight(index + 1);
            });

            star.addEventListener("click", () => {
                rating = index + 1;
                ratingInput.value = rating;
                highlight(rating);
            });
        });

        ratingContainer.addEventListener("mouseleave", () => {
            highlight(rating);
        });

        function highlight(count) {
            stars.forEach((star, i) => {
                star.classList.toggle("filled", i < count);
            });
        }
    }

    // =============================
    // Copy Link
    // =============================
    document.querySelectorAll(".copy-link-btn").forEach(btn => {
        btn.addEventListener("click", function (e) {
            e.preventDefault();

            const link = this.getAttribute("data-link");

            navigator.clipboard.writeText(window.location.origin + link)
                .then(() => alert("Copied link!"))
                .catch(() => alert("Copy failed!"));
        });
    });

    // =============================
    // Confirm Delete Comment
    // =============================
    const popupConfirm = document.getElementById('popupConfirm');
    const confirmNo = document.getElementById('confirmNo');
    const deleteForm = document.getElementById('deleteForm');

    if (popupConfirm && deleteForm) {
        const confirmYes = deleteForm.querySelector('button[type="submit"]');
        const deleteBtns = document.querySelectorAll('[id="deleteCommentBtn"]');

        deleteBtns.forEach(btn => {
            btn.addEventListener('click', (e) => {
                e.preventDefault();

                const actionUrl = btn.getAttribute('data-action');
                if (!actionUrl) {
                    console.error('Delete button missing data-action!');
                    return;
                }

                deleteForm.setAttribute('action', actionUrl);
                deleteForm.setAttribute('method', 'post');

                popupConfirm.classList.remove('hidden');
            });
        });

        if (confirmYes) {
            confirmYes.addEventListener('click', (e) => {
                e.preventDefault();

                const actionUrl = deleteForm.getAttribute('action');
                if (!actionUrl) {
                    console.error('Form action chưa set!');
                    return;
                }

                deleteForm.submit();
            });
        }

        if (confirmNo) {
            confirmNo.addEventListener('click', () => {
                popupConfirm.classList.add('hidden');
            });
        }

        popupConfirm.addEventListener('click', (e) => {
            if (e.target === popupConfirm) {
                popupConfirm.classList.add('hidden');
            }
        });
    }

});
