import React from 'react'
import {motion} from 'framer-motion';
import { slideUpVariants, zoomInVariants } from './animation';
import {planning} from './export';

const ThingsDo = () => {
  return (
    <div id='ThingsDo' className='w-full bg-white'>
      <motion.div
       initial="hidden" whileInView="visible" variants={slideUpVariants} className=' lg:w-[80%] w-[90%] m-auto py-[60px] flex flex-col justify-between items-center gap-[20px]'>
        <motion.h1 variants={slideUpVariants} className='text-red-500 text-2xl'>
        מעוניינים?
        </motion.h1>
        <motion.h1 variants={slideUpVariants} className='text-black text-[40px] text-center font-bold'>
          איך אפשר לעזור?
        </motion.h1>
        <motion.div className='w-[120px] h-[6px] bg-red-500' variants={slideUpVariants}></motion.div>
        <motion.div initial="hidden" whileInView="visible" variants={zoomInVariants} className='w-full grid lg:grid-cols-4 grid-cols-1 justify-center items-start gap-[20px] mt-[30px]'>
        {
            planning.map((item,index) => (
              <div key={index} className='flex flex-col items-center gap-5 p-6 border-2 border-red-500 rounded-lg w-full h-full relative'>
                <div className="absolute top-6 left-1/2 transform -translate-x-1/2 w-[80px] h-[80px] bg-red-500 rounded-full overflow-hidden flex items-center justify-center border-2 border-black">
                  <img
                    src={item.icon}
                    alt="icon"
                    className="w-[60px] h-[60px] object-cover"
                  />
                </div>
                <div className="pt-[100px] text-center"> {/* Add padding to push content below icon */}
                  <h1 className='text-2xl font-bold uppercase mb-4'>{item.title}</h1>
                  <p className='text-[20px] text-gray-600'>{item.about}</p>
                </div>
              </div>
            ))
          }
        </motion.div>
      </motion.div>
    </div>
  )
}

export default ThingsDo;