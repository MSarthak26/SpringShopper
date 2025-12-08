import { configureStore } from "@reduxjs/toolkit";
import productReducer from "./productReducer";
import { errorReducer } from "./errorReducrs";


export const store = configureStore({
    reducer:{
        products : productReducer,
        errors : errorReducer,

    },
    preloadedState:{},
})

export default store