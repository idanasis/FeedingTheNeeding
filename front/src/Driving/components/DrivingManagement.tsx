import React, { ChangeEvent, useEffect, useState } from 'react';
import {
  DndContext,
  closestCenter,
  PointerSensor,
  useSensor,
  useSensors,
  TouchSensor,
} from '@dnd-kit/core';
import {
  rectSortingStrategy,
  SortableContext,
  verticalListSortingStrategy,
} from '@dnd-kit/sortable';
import { useDraggable,useDroppable  } from '@dnd-kit/core';
import ResponsiveDatePickers from '../../Social/components/ResponsiveDatePickers';
import { Box, Card, CardContent, Typography, Select, MenuItem, Container, Button, IconButton, Fab } from '@mui/material';
import { Visit } from '../models/Visit';
import { Route } from '../models/Route';
import "../styles/Driving.css";
import ArrowDownwardIcon from '@mui/icons-material/ArrowDownward';
import ArrowUpwardIcon from '@mui/icons-material/ArrowUpward';
import AddIcon from '@mui/icons-material/Add';
import { addDriverConstraints, addRoute, deleteRoute, getDriversConstraints, getNeedersHere, getRoutes, submitAllRoutes, submitRoute, updateRoute } from '../../Restapi/DrivingRestapi';
import { Donor } from '../models/Donor';
import dayjs from 'dayjs';
import { getNearestFriday } from '../../commons/Commons';
import { DriverConstraints } from '../models/DriverConstraints';
import AddDriverOption from './AddDriverOption';
import { getDonorApproved } from '../../Restapi/DrivingRestapi';
import DiveHeader from '../../GoPage/DiveHeader';


const initialData = {
  routes: [
  ] as Route[],
  pickup: [
    {
      address: '123 Main St',
      firstName: 'John',
      lastName: 'Doe',
      phoneNumber: '123-456-7890',
      maxHour: 16,
      status: "Pickup"
    },
    {
      address: '456 Park Ave',
      firstName: 'Jane',
      lastName: 'Smith',
      phoneNumber: '987-654-3210',
      maxHour: 20,
      status: "Pickup"
    },
    {
      address: '456 Park Ass',
      firstName: 'johnie',
      lastName: 'mitchell',
      phoneNumber: '987-654-3210',
      maxHour: 19,
      status: "Pickup"
    },
    {
      address: '123 exce',
      firstName: 'jennifer',
      lastName: 'relee',
      phoneNumber: '987-654-2340',
      maxHour: 18,
      status: "Pickup"
    },
  ],
  drop: [] as Visit[],
};
export interface Data {
  routes: Route[],
  pickup: Visit[],
  drop: Visit[]
}
const Draggable = ({ id, children }: { id: string; children: React.ReactNode }) => {
  const { attributes, listeners, setNodeRef, transform } = useDraggable({ id });
  
  const style = {
    transform: transform
      ? `translate3d(${transform.x}px, ${transform.y}px, 0)`
      : undefined,
    marginBottom: '8px',
    touchAction:'none',
    'Button[dataNoDrag]': {
      pointerEvents: 'auto', /* Ensure the button is still clickable */
    }
  };

  return (
    <div ref={setNodeRef} {...listeners} {...attributes} style={style} >
      {children}
    </div>
  );
};

const Droppable = ({ id, children }: { id: string; children: React.ReactNode }) => {
  const { setNodeRef } = useDroppable({ id });

  return (
    <div ref={setNodeRef} style={{touchAction:'none',minHeight: '100px', padding: '8px', border: '1px dashed gray' }}>
      {children}
    </div>
  );
};


const DrivingManager = () => {
  const [data, setData] = useState<Data>(initialData);
  const [driver,setDrivers]=useState<DriverConstraints[]>([]);
  const [date, setDate] = useState<Date>(getNearestFriday(dayjs(Date.now())).toDate());
  const [donors, setDonors] = useState<Donor[]>([]);
  const [visible,setVisible]=useState(false);
  async function fetchDrivers(currentDate:Date=date) {
    try{
      const data=await getDriversConstraints(currentDate);
      setDrivers(data)
      let donors=await getDonorApproved();
      donors=donors.filter(donor=>data.every(driver=>driver.driverId!==donor.id));
      setDonors(donors);
    }catch(err){
      alert("תקלה בהצגת הנתונים");
    }
  }
  async function getDrops(currentDate:Date=date){
    try{
      const data=await getNeedersHere(currentDate);
        const updatedData={...initialData};
        updatedData.drop=data;
        const routes=await getRoutes(date);
        updatedData.routes=routes;
        updatedData.drop = updatedData.drop.filter((visit: Visit) =>
          !routes.some((route: Route) =>
            route.visit.some((v: Visit) => v.phoneNumber === visit.phoneNumber)
          )
        );        
        setData(updatedData);
      }catch(err){
        alert("תקלה בהצגת הנתונים");
        console.error(err);
      }
  }
  useEffect(() => {
          fetchDrivers();
          getDrops();
      }, [date]);
  const handleDateChange = (newDate:dayjs.Dayjs|null ) => {
          const d =newDate===null?dayjs(Date.now()).toDate():newDate.toDate();
          setDate(d);
          fetchDrivers(d);
          getDrops(d);
        };
  const sensors = useSensors(
    useSensor(PointerSensor, {
      activationConstraint: {
        distance: 10, // Prevent drag unless the pointer moves 10px
      },
      onStart: (event: { target: HTMLElement; }) => {
        const element = event.target as HTMLElement;
        if (element.getAttribute("dataNoDrag")) {
          return false; // Cancel drag for excluded elements
        }
      },
    }),
    useSensor(TouchSensor, { activationConstraint: { delay: 100, tolerance: 10 } })
  );

  const handleDragEnd = async({ active, over }: { active: any; over: any }) => {
    if (!over) return;

    const [sourceContainer, sourceIndex] = active.id.split('-') as [keyof typeof data, string];
    const [targetContainer, targetIndex] = over.id.split('-') as [keyof typeof data, string];
    if (sourceContainer === targetContainer) return;

    // Moving visit from pickup/drop to a route
    if (targetContainer === 'routes' && (sourceContainer === 'pickup' || sourceContainer === 'drop')) {
      const routeIndex = parseInt(targetIndex);
      const sourceItems = [...data[sourceContainer]];
      const movedItem = sourceItems.splice(parseInt(sourceIndex), 1)[0];
      
      const updatedRoutes = [...data.routes];
      const route=updatedRoutes[routeIndex]
      movedItem.priority=route.driverId===0?route.visit.length+1:route.visit.length;
      movedItem.note=movedItem.notes;
      route.visit.push(movedItem as Visit);
      await updateRoute(route);
      setData({
        ...data,
        [sourceContainer]: sourceItems,
        routes: updatedRoutes,
      });
    }
    else if (sourceContainer.startsWith('r') && targetContainer.startsWith('r')) {
      const [sourceContainer, sourceIndex,visit,visitIndex] = active.id.split('-') as [keyof typeof data, string,string,string];
      const [targetContainer, targetIndex,visit2,visitIndex2] = over.id.split('-') as [keyof typeof data, string,string,string];
      const updatedRoutes = [...data.routes];
      const curr=updatedRoutes[parseInt(sourceIndex)].visit.splice(parseInt(visitIndex), 1)[0];
      const destRoute=updatedRoutes[parseInt(targetIndex)];
      const sourceRoute=updatedRoutes[parseInt(sourceIndex)];
      const length=destRoute.visit.length+1;
      let newRoute: Visit[] = new Array(length);
      
      for(let i=0;i<length;i++){
        newRoute[i]=destRoute.visit[i];
      }
      for(let i=1;i<sourceRoute.visit.length;i++){
        if(sourceRoute.visit[i].priority!==i)
          sourceRoute.visit[i].priority=(sourceRoute.visit[i].priority as number) -1;
      }
      curr.priority=destRoute.driverId===0?destRoute.visit.length+1:destRoute.visit.length;;
      newRoute[length-1]=curr;
      destRoute.visit=newRoute;
      await updateRoute(sourceRoute);
      await updateRoute(destRoute);
      updatedRoutes[parseInt(targetIndex)].visit=newRoute;
      updatedRoutes[parseInt(sourceIndex)]=sourceRoute; 
      setData({
        ...data,
        routes: updatedRoutes,
      });
    }
  };

  const renderVisit = (visit: Visit) => (
    <Card variant="outlined">
      <CardContent>
        <Typography variant="h6">
          {visit.firstName} {visit.lastName}
        </Typography>
        <Typography variant="body2">{visit.address}</Typography>
        <Typography variant="body2">{visit.phoneNumber}</Typography>
        <Typography variant="body2">שעת הגעה/סיום: {visit.maxHour+":00"}</Typography>
        <Typography variant="body2">הערות: {visit.note?visit.note:visit.notes}</Typography>
        {visit.dietaryPreferences?<Typography variant="body2">{visit.dietaryPreferences}</Typography>:null}
      </CardContent>
    </Card>
  );
  const upButton =(index:number,idx:number,route: Route)=>{
    if(idx!==0&&(idx!==1||route.driverId===undefined||route.driverId===0)){
      return <IconButton color="primary" aria-label="up" data-no-drag onClick={(e)=>{
        e.stopPropagation();
        handleUp(index,idx)}} >
      <ArrowUpwardIcon />
    </IconButton>
    }
    return null;
  }
  const downButton =(index:number,idx:number,route: Route)=>{
    if(idx!==route.visit.length-1&&(idx!==0||route.driverId===undefined||route.driverId===0)){
      return <IconButton color="primary" aria-label="down" data-no-drag onClick={(e) => {
        e.stopPropagation();
        handleDown(index,idx)
      }}>
      <ArrowDownwardIcon />
    </IconButton>
    }
    return null;
  }
  const handleUp =async(index:number,idx: number)=>{
    const updatedRoutes = [...data.routes];
    const length=updatedRoutes[index].visit.length;
    const currRoute=Array.from(updatedRoutes[index].visit);
    const curr=updatedRoutes[index].visit.splice(idx, 1)[0];
    curr.priority=curr.priority!-1;
    let newRoute: Visit[] = new Array(length);
    let next=updatedRoutes[index].visit.splice(idx-1, 1)[0];
    next.priority=next.priority!+1;
    for(let i=0;i<length;i++){
      if (i===idx-1){
        newRoute[i]=curr;
      }else if(i===idx){
        newRoute[i]=next;
      }
      else{
      newRoute[i]=currRoute[i];
      }
    }
    const route=updatedRoutes[index];
    route.visit =newRoute;
    await updateRoute(route);
    updatedRoutes[index]=route;
    setData({
      ...data,
      routes: updatedRoutes,
    });
  }
  const handleDown =async(index:number,idx: number)=>{
    const updatedRoutes = [...data.routes];
    const length=updatedRoutes[index].visit.length;
    const currRoute=Array.from(updatedRoutes[index].visit);
    const curr=updatedRoutes[index].visit.splice(idx, 1)[0];
    curr.priority=curr.priority!+1;
    let newRoute: Visit[] = new Array(length);
    let next=updatedRoutes[index].visit.splice(idx, 1)[0];
    next.priority=next.priority!-1;
    for(let i=0;i<length;i++){
      if (i===idx+1){
        newRoute[i]=curr;
      }else if(i===idx){
        newRoute[i]=next;
      }
      else{
      newRoute[i]=currRoute[i];
      }
    }
    const route=updatedRoutes[index];
    route.visit =newRoute;
    await updateRoute(route);
    updatedRoutes[index]=route;
    setData({
      ...data,
      routes: updatedRoutes,
    });
  }
  const handleAddRoute = async() => {
    const route=await addRoute(date);
    const updatedRoutes = [...data.routes];
    updatedRoutes.push(route);
    setData({
      ...data,
      routes: updatedRoutes,
    });
  }
  const handleDriverChange = async(e: React.ChangeEvent<{ value: unknown }>, index: number) => {
    try{
    const route=data.routes[index] as Route;
    route.driverId=parseInt(e.target.value as string);
    const updatedRoutes = [...data.routes];
    route.driver = driver.find(d => d.driverId === parseInt(e.target.value as string)) as DriverConstraints;
    route.driverId = parseInt(e.target.value as string);
    const visit={address:updatedRoutes[index].driver?.startLocation as string,firstName:updatedRoutes[index].driver?.driverFirstName as string,lastName:updatedRoutes[index].driver?.driverLastName as string,phoneNumber:updatedRoutes[index].driver?.driverPhone as string,maxHour:updatedRoutes[index].driver?.endHour,note:updatedRoutes[index].driver?.requests,status:"Start",priority:0,};
    route.visit.unshift(visit);
    await updateRoute(route);
    updatedRoutes[index]=route;
    setData({ ...data, routes: updatedRoutes });
    }catch(err){
      alert('תקלה בשמירת הנתונים');
    }
  }
  const handlePublish = async(index:number) => {
    try{
    await submitRoute(data.routes[index]);
    const updatedData={...data};
    if(updatedData.routes[index].driverId===0){
      alert('נא לבחור נהג');
      return;
    }
    updatedData.routes[index].submitted=true;
    setData(updatedData);
    }catch(err){
      alert('תקלה בשמירת הנתונים');
    }
  }
  const handlePublishAll = async() => {
    try{
    
    const updatedData={...data};
    if(updatedData.routes.some(route=>route.driverId===0)){
      alert('נא לבחור נהגים לכל המסלולים או למחוק אותם');
      return;
    }
    await submitAllRoutes(date);
    updatedData.routes.forEach(route=>route.submitted=true);
    setData(updatedData);
    alert('מסלולים פורסמו בהצלחה');
    }catch(err){
      alert('תקלה בשמירת הנתונים');
  }
}
const addConstraint = async (donor:Donor) => {
  await addDriverConstraints({date: date, driverId: donor.id, startLocation: donor.address, endHour: 20, requests: "",driverPhone:donor.phoneNumber,driverFirstName:donor.firstName,driverLastName:donor.lastName,startHour:0});
  fetchDrivers();
}
const removeRoute = async(index:number)=>{
  const updatedRoutes = [...data.routes];
  const route=updatedRoutes[index];
  await deleteRoute(route.routeId);
  await getDrops()
}
  return (
    <div style={{overflowY: 'auto',backgroundColor: "snow"}}>
      <DiveHeader/>
     <div style={{marginTop: "20px",backgroundColor: "snow",justifySelf: "center",display: "flex",flexDirection: "row",justifyContent: "space-between",  gap: "10px"}}>
    <ResponsiveDatePickers onDateChange={handleDateChange}/>
    {!data.routes.every((route)=>route.submitted)?<Button variant="contained" color="primary" sx={{marginRight:10}} onClick={handlePublishAll} >פרסם הכל</Button>:null}
    <Button variant="contained" color="primary" onClick={()=>{setVisible(true)}}>הוסף נהג</Button>
    {visible?<AddDriverOption donors={donors} onClose={()=>{setVisible(false)}} onClick={addConstraint}/>:null}
    </div>
    <DndContext sensors={sensors} collisionDetection={closestCenter} onDragEnd={handleDragEnd}>
      <Container maxWidth="lg" style={{ marginTop: '20px' }}>
        <Box display="flex" justifyContent="space-between" gap={2}>
          {/* Pickup Container */}
          <Box flex={2} sx={{marginTop: '5px'}}>
            <Typography variant="h5" align="center">
              תורמים
            </Typography>
            <SortableContext items={data.pickup.map((_, idx) => `pickup-${idx}`)} strategy={verticalListSortingStrategy}>
              {data.pickup.map((visit, index) => (
                <Draggable key={`pickup-${index}`} id={`pickup-${index}`}>
                  {renderVisit(visit)}
                </Draggable>
              ))}
            </SortableContext>
          </Box>

          {/* Routes Container */}
          <Box flex={8} sx={{marginTop: '5px',width: '60%'}}>
            <Typography variant="h5" align="center">
              מסלולים
            </Typography>
            {data.routes.map((route, index) => (
              <Droppable key={`route-${index}`} id={`routes-${index}-visit-${route.visit.length+1}`}>
              <Card key={`route-${index}`} style={{ marginBottom: '16px', padding: '8px' }}>
                <Typography variant="h6">סיבוב {index+1}</Typography>
                <Select
                  value={route.driverId||'לא נבחר'}
                  label="Driver"
                  onChange={async(e:any) => {await handleDriverChange(e, index);        
                  }}
                >
                  {driver.map((driver, index) => (
                    <MenuItem key={index} value={driver.driverId}>{driver.driverFirstName+' '+driver.driverLastName}</MenuItem>
                  ))}
                <MenuItem key="0" value="לא נבחר">לא נבחר</MenuItem>
                </Select>
                <Typography variant="body2">{route.submitted===true?"פורסם":"טרם פורסם"}</Typography>
                {!route.submitted?<Button variant="contained" color="primary" onClick={()=>{handlePublish(index)}}>פרסם</Button>:null}
                {!route.submitted?<Button variant="contained" color="error" onClick={()=>{removeRoute(index)}}>מחק</Button>:null}
                <SortableContext
                  items={route.visit.map((_, idx) => `route-${index}-visit-${idx}`)}
                  strategy={verticalListSortingStrategy}
                >
                  {route.visit.map((visit, idx) => (
                    <Draggable key={`route-${index}-visit-${idx}`} id={`route-${index}-visit-${idx}`}>
                      {renderVisit(visit)}
                  {upButton(index,idx,route)}
                  {downButton(index,idx,route)}
                    </Draggable> 
                  ))}       
                </SortableContext>
              </Card>
              
              </Droppable>
            ))}
            <Fab color="primary" aria-label="add" onClick={handleAddRoute}>
            <AddIcon />
          </Fab>
          </Box>

          {/* Drop Container */}
          <Box flex={2} sx={{marginTop: '5px'}}>
            <Typography variant="h5" align="center">
              נזקקים
            </Typography>
            <SortableContext items={data.drop.map((_, idx) => `drop-${idx}`)} strategy={verticalListSortingStrategy}>
              {data.drop.map((visit, index) => (
                <Draggable key={`drop-${index}`} id={`drop-${index}`}>
                  {renderVisit(visit)}
                </Draggable>
              ))}
            </SortableContext>
          </Box>
        </Box>
      </Container>
    </DndContext>
    </div>
  );
};

export default DrivingManager;
