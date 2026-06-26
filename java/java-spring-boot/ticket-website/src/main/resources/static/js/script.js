function startCountdown() {
    const countdownEl = document.getElementById("countdown");
    if (!countdownEl) return;

    const stopTimeStr = countdownEl.dataset.stopTime;
    const stopTime = new Date(stopTimeStr).getTime();

    const hh = document.getElementById("hh");
    const mm = document.getElementById("mm");
    const ss = document.getElementById("ss");

    const buyBtn = document.querySelector("button.bg-primary");

    function updateCountdown() {
        const now = new Date().getTime();
        const diff = stopTime - now;

        if (diff <= 0) {
            hh.innerText = "00";
            mm.innerText = "00";
            ss.innerText = "00";

            // if (buyBtn) {
            //     buyBtn.disabled = true;
            //     buyBtn.innerText = "Ticket sales closed";
            //     buyBtn.classList.add("opacity-50", "cursor-not-allowed");
            // }
            return;
        }

        const hours = Math.floor(diff / (1000 * 60 * 60));
        const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
        const seconds = Math.floor((diff % (1000 * 60)) / 1000);

        hh.innerText = String(hours).padStart(2, "0");
        mm.innerText = String(minutes).padStart(2, "0");
        ss.innerText = String(seconds).padStart(2, "0");
    }

    updateCountdown();
    setInterval(updateCountdown, 1000);
}

document.addEventListener("DOMContentLoaded", startCountdown);


// tang giam
document.querySelectorAll(".ticket-card").forEach(card => {

    const minusBtn = card.querySelector(".qty-minus");
    const plusBtn = card.querySelector(".qty-plus");
    const qtyInput = card.querySelector(".qty-input");

    minusBtn.addEventListener("click", () => {

        let qty = parseInt(qtyInput.value);

        if (qty > 1) {
            qty--;
            qtyInput.value = qty;
        }

    });

    plusBtn.addEventListener("click", () => {

        const max = parseInt(card.dataset.max);       // lấy data-max
        let qty = parseInt(qtyInput.value);
        if (qty < max) {
            qty++;
            qtyInput.value = qty;
        }


    });

});

// popup Buy
const modal = document.getElementById("buyNowModal");

const currentTicketId = document.getElementById("currentTicketId");
const modalTicketType = document.getElementById("modalTicketType");
const modalTicketPrice = document.getElementById("modalTicketPrice");
const modalTicketQty = document.getElementById("modalTicketQty");
const modalSubtotal = document.getElementById("modalSubtotal");
const modalTotal = document.getElementById("modalTotal");

document.querySelectorAll(".open-buy-popup").forEach(btn => {

    btn.addEventListener("click", () => {

        const card = btn.closest(".ticket-card");

        const ticketId = parseInt(card.dataset.id);
        const ticketName = card.dataset.name;
        const ticketPrice = parseFloat(card.dataset.price);

        const qty = parseInt(card.querySelector(".qty-input").value);

        const total = ticketPrice * qty;

        // set popup data
        currentTicketId.value = ticketId;
        modalTicketType.textContent = ticketName;
        modalTicketPrice.textContent = Number(ticketPrice).toLocaleString('vi-VN');
        modalTicketQty.textContent = qty;
        modalSubtotal.textContent = Number(total).toLocaleString('vi-VN');
        modalTotal.textContent = Number(total).toLocaleString('vi-VN');

        document.getElementById("currentTicketId").value = ticketId;
        document.getElementById("currentTicketPrice").value = ticketPrice;
        document.getElementById("currentTicketName").value = ticketName;
        document.getElementById("currentTicketQty").value = qty;


        // show modal
        modal.classList.remove("hidden");
        modal.classList.add("flex");

    });

});

document.getElementById("closeBuyModal").addEventListener("click", () => {
    modal.classList.add("hidden");
});


document.addEventListener("DOMContentLoaded", () => {

    const cartModal = document.getElementById("addCartModal");

    const cartType = document.getElementById("modalCartType");
    const cartPrice = document.getElementById("modalCartPrice");
    const cartQty = document.getElementById("modalCartQty");
    const cartSubtotal = document.getElementById("modalSubtotalCart");
    const cartTotal = document.getElementById("modalTotalCart");

    document.querySelectorAll(".open-cart-popup").forEach(btn => {

        btn.addEventListener("click", () => {

            const card = btn.closest(".ticket-card");

            const ticketId = card.dataset.id;
            const ticketName = card.dataset.name;
            const ticketPrice = parseFloat(card.dataset.price);
            const qty = parseInt(card.querySelector(".qty-input").value);

            const total = ticketPrice * qty;

            // set data to modal
            cartType.textContent = ticketName;
            cartPrice.textContent = Number(ticketPrice).toLocaleString('vi-VN');
            cartQty.textContent = qty;
            cartSubtotal.textContent = Number(total).toLocaleString('vi-VN');
            cartTotal.textContent = Number(total).toLocaleString('vi-VN');

            document.getElementById("cartTicketId").value = ticketId;
            document.getElementById("cartQuantity").value = qty;

            // show modal
            cartModal.classList.remove("hidden");
            cartModal.classList.add("flex");

        });

    });

});

document.getElementById("closeCartModal").addEventListener("click", () => {

    const cartModal = document.getElementById("addCartModal");

    cartModal.classList.add("hidden");
    cartModal.classList.remove("flex");

});


const cartModal = document.getElementById("addCartModal");

cartModal.addEventListener("click", (e) => {

    if (e.target === cartModal) {
        cartModal.classList.add("hidden");
        cartModal.classList.remove("flex");
    }

});

modal.addEventListener("click", (e) => {

    if (e.target === modal) {
        modal.classList.add("hidden");
        modal.classList.remove("flex");
    }

});

document.querySelector(".copy-link-btn").onclick = () => {
    navigator.clipboard.writeText(window.location.href);
};

// document.getElementById("confirmBuyBtn").addEventListener("click", async () => {
//
//     const ticketId = document.getElementById("currentTicketId").value;
//     const ticketName = document.getElementById("modalTicketType").innerText;
//     const price = document.getElementById("modalTicketPrice").innerText.replace("$","");
//     const qty = document.getElementById("modalTicketQty").innerText;
//
//     const params = new URLSearchParams();
//
//     params.append("ticketTypeId", ticketId);
//     params.append("ticketName", ticketName);
//     params.append("price", price);
//     params.append("quantity", qty);
//
//     const token = document.querySelector("meta[name='_csrf']").content;
//     const header = document.querySelector("meta[name='_csrf_header']").content;
//     const response = await fetch("/checkout/add",{
//         method:"POST",
//         headers:{
//             "Content-Type":"application/x-www-form-urlencoded"
//         },
//         body: params
//     });
//
//     // UX: đóng popup
//     document.getElementById("buyNowModal").classList.add("hidden");
//     const result = await response.text();
//     // toast
//     if(result === "error" || result === "error 2" || result === "error 3"){
//         alert("Exceeded max ticket per user");
//     } else {
//         alert("Ticket added to checkout");
//     }
// });