import React, { useState, useEffect } from 'react';
import HomePage from './Home/HomePage';
import NeederTracking from './SocialManager/components/NeederTracking';
import NeedyPendingTable from './NeedyPending/Components/needyPending';
import { BrowserRouter as Router, Routes, Route, Navigate, useLocation } from 'react-router-dom';
import Login from "./Login/Components/Login";
import DonorRegister from "./DonorRegister/Components/DonorRegister";
import UploadConstraints from "./CookConstraints/Components/UploadConstraints";
import DrivingManager from "./DrivingManager/components/DrivingManagement";
import Donation from "./Donations/Components/Donation";
import DonorPendingTable  from "./DonorPending/Components/DonorPending"
import DonorTable from "./DonorsPage/Components/Donors"
import NeederTable from "./NeediesPage/Components/Needers"
import DriverConstraints from "./DrivingConstraints/Components/DriverConstraints";
import ConstraintsView from "./DonorsAssignments/Components/constraintsView";
import CookManager from "./CookingManager/Components/CookManager";
import GoPage from "./GoPage/GoPage";
import EditDonorDetails from "./EditDonor/Components/EditDonorDetails";
import Landing from "./LandingPage/Components/Landing";
import Video from "./LandingPage/Components/VideoPage";
import { HelmetProvider } from 'react-helmet-async';

import PicturesPage from './Home/components/PicturesPage';
import {getUserRoleFromJWT} from "./GoPage/goPageAPI";

// Protected route wrapper component
const ProtectedRoute = ({ children, allowedRoles, role }) => {

  const isAuthenticated = !!localStorage.getItem('token');
  const location = useLocation();


  // Public routes that don't require authentication
  const publicRoutes = ['/', '/login', '/donorRegister', '/donation'];
  
  // Routes that logged-in users shouldn't access
  const noAuthRoutes = ['/login', '/donorRegister'];
  
  // Donor-specific routes
  const donorRoutes = ['/', '/donation', '/cookConstraints', '/driversConstraints', '/donorConstraints', '/go', '/editDonorDetails'];


  // Check if current path is public
  const isPublicRoute = publicRoutes.includes(location.pathname);

  if(isAuthenticated && (location.pathname === '/login' | location.pathname === '/donorRegister')){
    return <Navigate to="/" replace />;
  }

  // If not authenticated and trying to access protected route
  if (!isAuthenticated && !isPublicRoute) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  // If authenticated and trying to access login/register
  if (isAuthenticated && noAuthRoutes.includes(location.pathname)) {
    return <Navigate to="/" replace />;
  }

  // If user is DONOR, check if they have access to the route
  if (role === 'DONOR' && !donorRoutes.includes(location.pathname)) {
    return <Navigate to="/" replace />;
  }

  // Check if user has required role to access the route
  if (allowedRoles && !allowedRoles.includes(role)) {
    return <Navigate to="/" replace />;
  }

  return children;
};

const App = () => {
  const [role, setRole] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchRole = async () => {
      try {
        const token = localStorage.getItem('token');
        if (!token) {
          console.warn('No token found in localStorage.');
          setRole(null);
          setLoading(false);
          return;
        }

        const userRole = await getUserRoleFromJWT(token); // Your function to decode JWT
        setRole(userRole);
      } catch (error) {
        console.error('Error fetching role:', error);
        setRole(null);
      } finally {
        setLoading(false);
      }
    };

    fetchRole();
  }, []); // Empty dependency array means this runs once on mount

  if (loading) {
    return <div>Loading...</div>; 
  }

  return (
    <HelmetProvider>
    <Router>
      <Routes>
        <Route path="/landing" element={<Landing />} />
        <Route path="/video" element={<Video />} />
        {/* Public routes */}
        <Route path="/" element={<HomePage />} />
          <Route
          path="/login"
          element={
            <ProtectedRoute role={role} allowedRoles={[null]}>
              <Login />
            </ProtectedRoute>
          }
        />
          <Route
          path="/donorRegister"
          element={
            <ProtectedRoute role={role} allowedRoles={[null]}>
              <DonorRegister />
            </ProtectedRoute>
          }
        />

        <Route path="/donation" element={<Donation />} />
        <Route path="/pictures" element={<PicturesPage />} />


        {/* Protected routes */}
        <Route
          path="/social"
          element={
            <ProtectedRoute role={role} allowedRoles={['STAFF', 'ADMIN']}>
              <NeederTracking />
            </ProtectedRoute>
          }
        />
        <Route
          path="/neederPending"
          element={
            <ProtectedRoute role={role} allowedRoles={['STAFF', 'ADMIN']}>
              <NeedyPendingTable />
            </ProtectedRoute>
          }
        />
        <Route
          path="/driving"
          element={
            <ProtectedRoute role={role} allowedRoles={['STAFF', 'ADMIN']}>
              <DrivingManager />
            </ProtectedRoute>
          }
        />
        <Route
          path="/cookConstraints"
          element={
            <ProtectedRoute role={role} allowedRoles={['STAFF', 'ADMIN', 'DONOR']}>
              <UploadConstraints />
            </ProtectedRoute>
          }
        />
        <Route
          path="/driversConstraints"
          element={
            <ProtectedRoute role={role} allowedRoles={['STAFF', 'ADMIN', 'DONOR']}>
              <DriverConstraints />
            </ProtectedRoute>
          }
        />
        <Route
          path="/donorConstraints"
          element={
            <ProtectedRoute role={role} allowedRoles={['STAFF', 'ADMIN', 'DONOR']}>
              <ConstraintsView />
            </ProtectedRoute>
          }
        />
        <Route
          path="/cookManager"
          element={
            <ProtectedRoute role={role} allowedRoles={['STAFF', 'ADMIN']}>
              <CookManager />
            </ProtectedRoute>
          }
        />
        <Route
          path="/donorPending"
          element={
            <ProtectedRoute role={role} allowedRoles={['STAFF', 'ADMIN']}>
              <DonorPendingTable />
            </ProtectedRoute>
          }
        />
        <Route
        path='/donors'
        element={
          <ProtectedRoute role={role} allowedRoles={['STAFF', 'ADMIN']}>
            <DonorTable />
          </ProtectedRoute>
        }
        />
        <Route
        path='/needers'
        element={
          <ProtectedRoute role={role} allowedRoles={['STAFF', 'ADMIN']}>
            <NeederTable />
          </ProtectedRoute>
        }
        />
        <Route
          path="/go"
          element={
            <ProtectedRoute role={role} allowedRoles={['STAFF', 'ADMIN', 'DONOR']}>
              <GoPage />
            </ProtectedRoute>
        }
        />
        <Route
          path="/editDonorDetails"
          element={
            <ProtectedRoute role={role} allowedRoles={['STAFF', 'ADMIN', 'DONOR']}>
              <EditDonorDetails />
            </ProtectedRoute>
          }
        />

      </Routes>
    </Router>
    </HelmetProvider>
  );
};

export default App;