import React from 'react'
import project1 from "../images/pic1.jpg";
import project2 from "../images/pic2.jpg";
import project3 from "../images/pic3.jpg";
import project4 from "../images/pic4.jpg";
import project5 from "../images/pic5.jpg";
import project6 from "../images/pic6.jpg";
import project7 from "../images/pic7.jpg";
import project8 from "../images/pic8.jpg";
import {motion} from 'framer-motion';
import { slideUpVariants ,zoomInVariants} from './animation';
const Pictures = () => {
  return (
   
    <div id='תמונות' className='w-full'>
    <motion.div
     initial="hidden" whileInView="visible" variants={slideUpVariants} className=' lg:w-[80%] w-[90%] m-auto py-[60px] flex flex-col justify-between items-center gap-[20px]'>
      <motion.h1 variants={slideUpVariants} className='text-red-500 text-2xl' >
        תמונות
      </motion.h1>
      <motion.h1 variants={slideUpVariants} className='text-white text-[40px] text-center font-bold' >
        רגעי שיא
      </motion.h1>
      <motion.div className='w-[120px] h-[6px] bg-red-500' variants={slideUpVariants}></motion.div>
      <motion.div  initial="hidden" whileInView="visible" variants={zoomInVariants} className='w-full grid lg:grid-cols-4 grid-cols-1 gap-[20px]'>
     <img src={project1} alt='project image' className='h-[250px] w-full' />
     <img src={project2} alt='project image' className='h-[250px] w-full' />
     <img src={project3} alt='project image' className='h-[250px] w-full' />
     <img src={project4} alt='project image' className='h-[250px] w-full' />
     <img src={project5} alt='project image' className='h-[250px] w-full' />
     <img src={project6} alt='project image' className='h-[250px] w-full' />
     <img src={project7} alt='project image' className='h-[250px] w-full' />
     <img src={project8} alt='project image' className='h-[250px] w-full' />
      </motion.div>
      </motion.div>
  </div>
  )
}

export default Pictures;