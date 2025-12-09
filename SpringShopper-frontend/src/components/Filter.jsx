import { MenuItem, Select, FormControl, InputLabel, Tooltip, Button } from "@mui/material";
import { current } from "@reduxjs/toolkit";
import { useEffect, useState } from "react";
import { FiArrowDown, FiArrowUp, FiRefreshCw, FiSearch } from "react-icons/fi";
import { useLocation, useNavigate, useSearchParams } from "react-router-dom";


const Filter = ({categories}) => {
    // const categories = [
    //     {categoryId : 1 , categoryName : "Electronics"},
    //     {categoryId : 2 , categoryName : "Clothing"},
    //     {categoryId : 3 , categoryName : "Furniture"},
    //     {categoryId : 4 , categoryName : "Books"},
    //     {categoryId : 5 , categoryName : "Toys"},
    // ]

    const [searchParmas] = useSearchParams();
    const params = new URLSearchParams(searchParmas)
    const pathname = useLocation().pathname;
    const navigate = useNavigate();

    const [category,setCategory] = useState("all");
    const [sortOrder,setSortOrder] = useState("asc");
    const [searchTerm,setSearchTerm] = useState("");

    useEffect(()=>{
        const currentCategory = searchParmas.get("category") || "all"
        const currentSortOrder = searchParmas.get("sortBy") || "asc"
        const currentSearchTerm = searchParmas.get("keyword") || ""

        setCategory(currentCategory)
        setSearchTerm(currentSearchTerm)
        setSortOrder(currentSortOrder)

    },[searchParmas])
    const handleCategoryChange = (event) =>{
        const selectedCategory = event.target.value
        if(selectedCategory == "all"){
            params.delete("category")
        }
        else{
            params.set("category",selectedCategory)
        }
        navigate(`${pathname}?${params}`)
        setCategory(event.target.value)
    }

    useEffect(()=>{
        const handler = setTimeout(() => {
            if (searchTerm) {
                searchParmas.set("keyword",searchTerm)
            } else {
                searchParmas.delete("keyword")
            }
            navigate(`${pathname}?${searchParmas.toString()}`)
        }, 700);
        return ()=>{
            clearTimeout(handler)
        }
    },[searchParmas,navigate,searchTerm,pathname])


    const toggleSortOrder = () => {
  setSortOrder((prevOrder) => {
    const newOrder = prevOrder === "asc" ? "desc" : "asc";
    params.set("sortBy",newOrder)
    navigate(`${pathname}?${params}`)
    return newOrder;
  });
};

    const handleClearFilter = ()=>{
        navigate({pathname:window.location.pathname})
    }
    return (
        <div className="flex lg:flex-row flex-col-reverse lg:justify-between justify-center items-center gap-4">
            
            <div className="relative flex items-center 2xl:w-[450px] sm:w-[420px] w-full">
                <input type="text"
                placeholder="Search products" 
                value={searchTerm}
                onChange={(e)=>setSearchTerm(e.target.value)}
                className="border border-gray-400 text-slate-800 rounded-md py-2 pl-10 pr-4 w-full focus:outline-none focus:ring-2 focus:ring-[#1967d2]"/>
                <FiSearch className="absolute left-3 text-slate-800 size={20}"/>
            </div>
             <div className="flex sm:flex-row flex-col gap-4 items-center">
                    <FormControl className="text-slate-800 border-slate-700"
                        variant="outlined"
                        size="small">

                            <InputLabel id = "category-select-label">Category</InputLabel>
                            <Select className="min-w-[120px] text-slate-800 border-slate-700"
                            labelId = "category-select-label"
                            value={category}
                            onChange={handleCategoryChange}
                            label="Category">
                                <MenuItem value="all">All</MenuItem>
                                {categories.map((item)=>(
                                    <MenuItem key = {item.categoryId} value={item.categoryName}>
                                        {item.categoryName}
                                        </MenuItem>
                                ))}
                            </Select>
                            
                    </FormControl>
                    
                    <Tooltip title="sorted by price : asc">
                        <Button onClick={toggleSortOrder}
                        variant="contained" color="primary" 
                        className="flex items-center gap-2 h-10">
                                Sort By 
                                {sortOrder === "asc" ? (<FiArrowUp size={20}/>) : (<FiArrowDown size={20}/>) }
                        </Button>
                    </Tooltip>
                    <button onClick={handleClearFilter}
                    className="flex items-center gap-2 bg-rose-900 text-white px-3 py-2 rounded-md transition duration-300 ease-in shadow-md focus:outline-none">
                                <FiRefreshCw className="font-semibold" size={16}/>
                                <span className="font-semibold"> Clear filter</span>
                    </button>
                </div>

        </div>
    )
}

export default Filter;