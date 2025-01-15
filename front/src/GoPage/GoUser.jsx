import React from 'react';
import { motion } from 'framer-motion';
import { Link } from 'react-router-dom';
import client1 from "../Home/images/avatar.webp";

const GoUser = () => {
  const userPages = [
    { name: "דף הפעילות שלי", path: "/my-activities" },
    { name: "לוח זמנים", path: "/my-schedule" },
    { name: "הודעות", path: "/my-messages" },
    { name: "פרופיל", path: "/profile" }
  ];

  const slideUpVariants = {
    hidden: { opacity: 0, y: 50 },
    visible: { opacity: 1, y: 0, transition: { duration: 0.5 } }
  };

  const zoomInVariants = {
    hidden: { opacity: 0, scale: 0.8 },
    visible: { opacity: 1, scale: 1, transition: { duration: 0.5 } }
  };

  return (
    <div id='GoUser' className='w-full bg-white'>
      <motion.div
        initial="hidden"
        whileInView="visible"
        variants={slideUpVariants}
        className='lg:w-[80%] w-[90%] m-auto py-[60px] flex flex-col justify-between items-center gap-[20px]'
      >
        <motion.h1
          variants={slideUpVariants}
          className='text-black text-[40px] text-center font-bold'
        >
          פעולות מתנדב
        </motion.h1>
        <motion.div
          className='w-[120px] h-[6px] bg-red-500'
          variants={slideUpVariants}
        ></motion.div>
        
        <motion.div
          initial="hidden"
          whileInView="visible"
          variants={zoomInVariants}
          className='w-full flex justify-center mt-[30px]'
        >
          <div className='flex flex-col items-center gap-5 p-6 border-2 border-red-500 rounded-lg w-full max-w-md relative'>
            <div className="absolute top-6 left-1/2 transform -translate-x-1/2 w-[80px] h-[80px] bg-red-500 rounded-full overflow-hidden flex items-center justify-center border-2 border-black">
              <img
                src={client1}
                alt="user icon"
                className="w-[60px] h-[60px] object-cover"
              />
            </div>
            
            <div className="pt-[100px] w-full flex flex-col gap-3">
              {userPages.map((page, index) => (
                <Link
                  key={index}
                  to={page.path}
                  className="text-gray-600 hover:text-red-500 text-xl text-center transition-colors duration-300 p-3 border-2 border-transparent hover:border-red-500 rounded-lg w-full"
                >
                  {page.name}
                </Link>
              ))}
            </div>
          </div>
        </motion.div>
      </motion.div>
    </div>
  );
};

export default GoUser;