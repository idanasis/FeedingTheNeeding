import React from 'react';
import Header from './DiveHeader';
import GoManager from './GoManager';
import GoUser from './GoUser';

const GoPage = () => {
    return (
        <>
          <Header/>
          <GoManager/>  
            <GoUser/>
   </>
    );
};

export default GoPage;