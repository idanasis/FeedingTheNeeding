import React from "react";
import { motion } from "framer-motion";
import { slideUpVariants, zoomInVariants } from "./animation";
import { people } from "./export";

const SayOnUs = () => {
  return (
    <div id="SayOnUs" className="w-full">
      <motion.div
        initial="hidden"
        whileInView="visible"
        variants={slideUpVariants}
        className="lg:w-[80%] w-[90%] m-auto py-[60px] flex flex-col justify-between items-center gap-[20px]"
      >
      
        <motion.h1
          variants={slideUpVariants}
          className="text-white text-[40px] text-center font-bold"
        >
          הרכזים שלנו
        </motion.h1>
        <motion.div
          className="w-[120px] h-[6px] bg-red-500"
          variants={slideUpVariants}
        ></motion.div>
        <motion.div
          initial="hidden"
          whileInView="visible"
          variants={zoomInVariants}
          className="lg:w-full w-[90%] grid lg:grid-cols-4 grid-cols-1 justify-center items-start gap-[30px] mt-[30px]"
        >
          {people.map((item, index) => (
            <div
              key={index}
              className="flex flex-col justify-center items-center"
            >
              <div className="border-2 border-white hover:bg-red-500 p-4 pb-[100px] pt-[30px] w-[230px] h-[300px] flex items-center justify-center">
                <p className="text-lg text-center hover:text-black text-white">{item.about}</p>
              </div>
              <div className="flex flex-col justify-center items-center gap-[5px]">
                <div className="w-[100px] h-[100px] mt-[-50px] rounded-full overflow-hidden">
                  <img
                    src={item.image}
                    alt="icon"
                    className="w-full h-full object-cover"
                    width="100"
                    height="100"
                  />
                </div>
                <h1 className="text-white text-27px font-semibold uppercase">{item.name}</h1>
                <h1 className="text-red-500 text-[22px]">{item.post}</h1>
              </div>
            </div>
          ))}
        </motion.div>
      </motion.div>
    </div>
  );
};

export default SayOnUs;