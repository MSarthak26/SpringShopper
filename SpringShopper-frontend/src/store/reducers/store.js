import { configureStore } from "@reduxjs/toolkit";
import productReducer from "./productReducer";
import { errorReducer } from "./errorReducrs";
import { cartReducer } from "./cartReducer";

const cartItems = localStorage.getItem("cartItems")
    ? JSON.parse(localStorage.getItem("cartItems")) : [];

const initialState = {
    carts : {cart : cartItems}
}

export const store = configureStore({
    reducer:{
        products : productReducer,
        errors : errorReducer,
        carts : cartReducer,

    },
    preloadedState: initialState,
})

export default store