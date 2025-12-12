import React from 'react'
import { useSelector } from 'react-redux'
import { Navigate, Outlet, useLocation } from 'react-router-dom';

const PrivateRoute = ({publicPage = false}) => {
    const {user} = useSelector((state) => state.auth)
     if (publicPage) {
        return user ? <Navigate to="/" /> : <Outlet />
    }

  return (
    <div>PrivateRoute</div>
  )
}

export default PrivateRoute