import React from 'react';
import HomePage from './Home/HomePage';
import NeederTracking from './Social/components/NeederTracking';
import NeedyPendingTable from './Social/components/needyPending';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Login from "./Authentication/Components/Login";
import DonorRegister from "./Authentication/Components/DonorRegister";

import UploadConstraints from "./Cooking/Components/UploadConstraints";
import DrivingManager from "./Driving/components/DrivingManagement";
import Donation from "./Donations/Components/Donation";
import DonorPendingTable  from "./User/components/DonorPending"




function App() {
  return (
  <Router>
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/social" element={<NeederTracking />} />
          <Route path="/neederPending" element={<NeedyPendingTable />} />
            <Route path="/login" element={<Login />} />
            <Route path="/donorRegister" element={< DonorRegister />} />
            <Route path="/driving" element={<DrivingManager />}/>
            <Route path="/donation" element={<Donation />}/>
            <Route path="/donorPending" element={<DonorPendingTable  />} />
        </Routes>
      </Router>
  );
}

export default App;