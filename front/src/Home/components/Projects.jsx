import React from 'react'
import {motion} from 'framer-motion';
import { slideUpVariants, zoomInVariants} from './animation';
import {allProjects} from './export';

const Projects = () => {
  return (
    <div id='פרוייקטים' className='w-full bg-white'>
      <motion.div
       initial="hidden" whileInView="visible" variants={slideUpVariants} className='lg:w-[80%] w-[90%] m-auto py-[60px] flex flex-col justify-between items-center gap-[20px]'>
        <motion.h1 variants={slideUpVariants} className='text-red-500 text-2xl'>
          חסדים בכל מיני צבעים
        </motion.h1>
        <motion.h1 variants={slideUpVariants} className='text-black text-[40px] text-center font-bold'>
          הפרוייקטים שלנו
        </motion.h1>
        <motion.div className='w-[120px] h-[6px] bg-red-500' variants={slideUpVariants}></motion.div>
        
        <motion.div 
          initial="hidden" 
          whileInView="visible" 
          variants={slideUpVariants} 
          className='w-full grid lg:grid-cols-3 grid-cols-1 justify-center items-center gap-[20px] mt-[30px]'
        >
          {allProjects.map((item, index) => (
            <motion.div
              variants={zoomInVariants}
              className='flex items-center justify-start gap-5 p-2 w-full'
              key={index}
            >
              <img 
                src={item.icon} 
                alt="icon" 
                className='w-[70px] h-[70px] border-2 border-red-500 hover:bg-red-500 rounded-lg p-2'
              />
              <div className='flex flex-col justify-center items-start flex-1'>
                <h1 className='text-xl font-bold text-black'>{item.title}</h1>
                <p className='text-[18px]'>{item.about}</p>
              </div>
            </motion.div>
          ))}
        </motion.div>
      </motion.div>
    </div>
  )
}

export default Projects;