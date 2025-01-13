import React, { useEffect, useState } from 'react';
import Header from './DiveHeader';
import GoManager from './GoManager';
import GoUser from './GoUser';
import {getUserRoleFromJWT} from './goPageAPI';

const GoPage = () => {
  const [role, setRole] = useState(null);

  useEffect(() => {
    const fetchRole = async () => {
      try {
        const token = localStorage.getItem('token'); // Replace 'authToken' with the actual key used in localStorage.
        if (!token) {
          console.warn('No token found in localStorage.');
          setRole(null);
          return;
        }

        const userRole = await getUserRoleFromJWT(token);
        setRole(userRole); 
      } catch (error) {
        console.error('Error fetching role:', error);
        setRole(null); 
      }
    };

    fetchRole();
  }, []);

  return (
    <>
      <Header />
      {(role === 'STAFF' || role=='ADMIN') && <GoManager />}
      <GoUser />
    </>
  );
};

export default GoPage;
