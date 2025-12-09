const initialState = {
    isLoading : false,
    errorMessage : null,
    categoryLoader : false,
    categoryError : null,
}

export const errorReducer = (state = initialState,action) =>{
    switch (action.type) {
        case "IS_FETCHING": 
            return {
                ...state,
                isLoading : true,
                errorMessage : null
            }
            
        case "IS_SUCCESS":
             return {
                ...state,
                isLoading : false,
                errorMessage : null
            }
            
        case "IS_ERROR":
             return {
                ...state,
                isLoading : false,
                errorMessage : "Failed to load products."
            }
         case "CATEGORY_LOADER":
             return {
                ...state,
                categoryLoader : true,
                categoryError : null,
                errorMessage : null
            }

        case "CATEGORY_SUCCESS":
             return {
                ...state,
                categoryLoader : false,
                categoryError : null,
                errorMessage : null
            }
            
        case "CATEGORY_ERROR":
             return {
                ...state,
                categoryLoader : false,
                categoryError : "Failed to load categories."
            }
        default:
            return state;
    }

}