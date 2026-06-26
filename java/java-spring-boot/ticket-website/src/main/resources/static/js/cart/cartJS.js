// function increase(cartId, itemId) {
//     fetch(`/cart/${cartId}/items/${itemId}/increase`,
//         {method: 'PUT'}
//     ).then(res => res.json())
//         .then(data => {
//             document.getElementById("qty-" + itemId).innerText = data.quantity;
//
//         })
//         .catch(err => console.error(err));
// }
//
// function decrease(cartId, itemId) {
//     fetch(`/cart/${cartId}/items/${itemId}/decrease`, {
//         method: 'PUT'
//     })
//         .then(res => res.json())
//         .then(data => {
//             if (data.quantity > 0) {
//                 document.getElementById("qty-" + itemId).innerText = data.quantity;
//             }
//             if (Number(data.quantity) == 0) {
//                 document.getElementById("wholeItem-" + itemId).remove();
//             }
//
//         })
//         .catch(err => console.error(err));
// }

