import React from 'react'
import Introduceimg from '../images/FeedingLogo.png';
import backgroundImage from '../images/samiNew.png';
import { motion } from 'framer-motion';
import { slideUpVariants, zoomInVariants } from './animation';
import { Link } from 'react-scroll';

const Introduce = () => {
  const handleButtonClick = (path) => {
    const token = localStorage.getItem('token');
    if (token) {
      window.location.href = '/go';
    } else {
      window.location.href = path;
    }
  };

  return (
    <div id='בית' className='bg-black w-full lg:h-[700px] h-fit m-auto pt-[60px] lg:pt-[0px] lg:px-[150px] px-[20px] flex justify-between items-center lg:flex-row flex-col lg:gap-5 gap-[50px] bg-cover bg-center' style={{backgroundImage: `url(${backgroundImage})`}}>
      <motion.div
        initial="hidden" whileInView="visible" variants={slideUpVariants} 
        className='lg:w-[60%] w-full flex flex-col justify-center items-start lg:gap-8 gap-4'
      >
        <motion.h1
          variants={slideUpVariants}
          className='text-red-500 text-2xl'
        >
        </motion.h1>
        <motion.h1 variants={slideUpVariants} className='text-red-700 text-[50px] font-bold'>
          להשביע את הלב
        </motion.h1>
        <motion.h1 variants={slideUpVariants} className='text-red-700 text-[28px] font-bold'>
          ארגון סיוע וחסד
        </motion.h1>
        <div className='w-[120px] h-[6px] bg-red-700'></div>
        <p className='text-red-700 text-[22px] font-bold'>הצטרפו לנתינה לעוד עשרות מתנדבים!</p>
        <motion.div
          initial="hidden" whileInView="visible" variants={zoomInVariants} 
          className='flex justify-center items-center gap-5'
        >
          <Link
            to="אודות"
            smooth={true}
            duration={500}
            offset={-70} // Adjust this value based on your navbar height
          >
            <motion.button
              variants={zoomInVariants}
              className='bg-red-500 hover:bg-white hover:text-black px-10 py-3 rounded-lg text-black font-bold cursor-pointer'
              style={{ fontSize: '20px' }}
              
            >
              קרא עוד!
            </motion.button>
          </Link>
          <motion.button
            variants={zoomInVariants}
            className='border-red-700 hover:border-red-700 hover:text-red-700 border-2 px-10 py-3 rounded-lg text-red-700 font-bold'
            style={{ fontSize: '20px' }}
            onClick={() => handleButtonClick('/login')}
          >
            בואו להתנדב!
          </motion.button>
        </motion.div>
      </motion.div>
      <div className='w-[40%] flex flex-col justify-end items-end'>
        <motion.img 
          initial="hidden" 
          whileInView="visible" 
          variants={zoomInVariants} 
          src={Introduceimg} 
          alt='Introduceimage' 
          className='lg:h-[300px] h-[300px] lg:mb-[-100px]' 
        />
      </div>
    </div>
  );
}

export default Introduce;