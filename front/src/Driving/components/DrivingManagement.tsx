import React, { useState } from 'react';
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


const initialData = {
  routes: [
    {id:1, driverId: 101, visits: [] ,status:"פורסם"},
    {id:2,  driverId: 102, visits: [],status:"פורסם" },
  ],
  pickup: [
    {
      address: '123 Main St',
      firstName: 'John',
      lastName: 'Doe',
      phoneNumber: '123-456-7890',
      maxHour: 16,
    },
    {
      address: '456 Park Ave',
      firstName: 'Jane',
      lastName: 'Smith',
      phoneNumber: '987-654-3210',
      maxHour: 20,
    },
    {
      address: '456 Park Ass',
      firstName: 'johnie',
      lastName: 'mitchell',
      phoneNumber: '987-654-3210',
      maxHour: 19,
    },
    {
      address: '123 exce',
      firstName: 'jennifer',
      lastName: 'relee',
      phoneNumber: '987-654-2340',
      maxHour: 18,
    },
  ],
  drop: [
    {
      address: '789 Elm St',
      firstName: 'Alice',
      lastName: 'Brown',
      phoneNumber: '555-123-4567',
      maxHour: 17,
    },
  ],
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

  const handleDragEnd = ({ active, over }: { active: any; over: any }) => {
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
      updatedRoutes[routeIndex].visits.push(movedItem as Visit);

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
      const curr=updatedRoutes[parseInt(sourceIndex)].visits.splice(parseInt(visitIndex), 1)[0];
      const destRoute=updatedRoutes[parseInt(targetIndex)];
      const sourceRoute=updatedRoutes[parseInt(sourceIndex)];
      const length=destRoute.visits.length+1;
      let newRoute: Visit[] = new Array(length);
      
      for(let i=0;i<length;i++){
        newRoute[i]=destRoute.visits[i];
      }
      newRoute[length-1]=curr;
      updatedRoutes[parseInt(targetIndex)].visits=newRoute;
 
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
        <Typography variant="body2">Phone: {visit.phoneNumber}</Typography>
      </CardContent>
    </Card>
  );
  const upButton =(index:number,idx:number,route: Route)=>{
    if(idx!==0){
      return <IconButton color="primary" aria-label="up" data-no-drag onClick={(e)=>{
        e.stopPropagation();
        handleUp(index,idx)}} >
      <ArrowUpwardIcon />
    </IconButton>
    }
    return null;
  }
  const downButton =(index:number,idx:number,route: Route)=>{
    if(idx!==route.visits.length-1){
      return <IconButton color="primary" aria-label="down" data-no-drag onClick={(e) => {
        e.stopPropagation();
        handleDown(index,idx)
      }}>
      <ArrowDownwardIcon />
    </IconButton>
    }
    return null;
  }
  const handleUp =(index:number,idx: number)=>{
    const updatedRoutes = [...data.routes];
    const length=updatedRoutes[index].visits.length;
    const currRoute=Array.from(updatedRoutes[index].visits);
    const curr=updatedRoutes[index].visits.splice(idx, 1)[0];
    let newRoute: Visit[] = new Array(length);
    let next=updatedRoutes[index].visits.splice(idx-1, 1)[0];
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
    updatedRoutes[index].visits =newRoute;
    setData({
      ...data,
      routes: updatedRoutes,
    });
  }
  const handleDown =(index:number,idx: number)=>{
    const updatedRoutes = [...data.routes];
    const length=updatedRoutes[index].visits.length;
    const currRoute=Array.from(updatedRoutes[index].visits);
    const curr=updatedRoutes[index].visits.splice(idx, 1)[0];
    let newRoute: Visit[] = new Array(length);
    let next=updatedRoutes[index].visits.splice(idx, 1)[0];
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
    updatedRoutes[index].visits =newRoute;
    setData({
      ...data,
      routes: updatedRoutes,
    });
  }

  return (
    <div style={{ marginTop: '50rem',overflowY: 'auto'}}>
    <ResponsiveDatePickers onDateChange={(date) => console.log(date)} />
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
              <Droppable key={`route-${index}`} id={`routes-${index}-visit-${route.visits.length+1}`}>
              <Card key={`route-${index}`} style={{ marginBottom: '16px', padding: '8px' }}>
                <Typography variant="h6">סיבוב נהג {route.driverId}</Typography>
                <Select
                  value={route.driverId}
                  label="Driver"
                  onChange={(e) => {
                    const updatedRoutes = [...data.routes];
                    updatedRoutes[index].driverId = parseInt(e.target.value as string);
                    setData({ ...data, routes: updatedRoutes });
                  }}
                >
                  <MenuItem value={101}>101</MenuItem>
                  <MenuItem value={102}>102</MenuItem>
                </Select>
                <Typography variant="body2">פורסם: {route.status}</Typography>
                <Button variant="contained" color="primary" >פרסם</Button>
                <SortableContext
                  items={route.visits.map((_, idx) => `route-${index}-visit-${idx}`)}
                  strategy={verticalListSortingStrategy}
                >
                  {route.visits.map((visit, idx) => (
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
            <Fab color="primary" aria-label="add">
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
