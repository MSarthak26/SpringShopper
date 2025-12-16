import { configureStore } from "@reduxjs/toolkit";
import productReducer from "./productReducer";
import { errorReducer } from "./errorReducrs";
import { cartReducer } from "./cartReducer";
import { authReducer } from "./authReducer";
import { paymentMethodReducer } from "./paymentMethodReducer";

const cartItems = localStorage.getItem("cartItems")
    ? JSON.parse(localStorage.getItem("cartItems")) : [];

const user = localStorage.getItem("auth")
    ? JSON.parse(localStorage.getItem("auth")) : null;

const selectUserCheckoutAddress = localStorage.getItem("CHECKOUT_ADDRESS")
    ? JSON.parse(localStorage.getItem("CHECKOUT_ADDRESS"))
    : [];

const initialState = {
    carts : {cart : cartItems},
    auth : { user : user, selectUserCheckoutAddress}
}

export const store = configureStore({
    reducer:{
        products : productReducer,
        errors : errorReducer,
        carts : cartReducer,
        auth : authReducer,
        payment : paymentMethodReducer,

    },
    preloadedState: initialState,
})

export default store