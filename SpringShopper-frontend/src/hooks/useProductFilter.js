import { useEffect } from "react";
import { useDispatch } from "react-redux";
import { useSearchParams } from "react-router-dom"
import {fetchProducts} from "../store/actions";

const useProductFilter = () => {
    const [searchParams] = useSearchParams();
    const dispatch = useDispatch();
    
    useEffect(()=>{
        const params = new URLSearchParams();
        const sortOrder = searchParams.get("sortBy") || "asc"
        const categoryParams = searchParams.get("category") || null
        const keyword = searchParams.get("keyword") || null

        const currentPage = searchParams.get("page") 
        ? Number(searchParams.get("page")) : 1;

        params.set("sortBy","price")
        params.set("sortOrder",sortOrder)
        params.set("pageNumber",currentPage - 1)

        if(categoryParams){
            params.set("category",categoryParams)
        }
        if(keyword){
            params.set("keyword",keyword)
        }

        const queryString = params.toString();
        console.log("Query String : " ,queryString)
        dispatch(fetchProducts(queryString))

    },[dispatch,searchParams])
    
}
export default useProductFilter;