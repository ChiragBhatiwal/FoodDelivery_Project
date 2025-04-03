import mongoose from "mongoose"
import orderModel from "../Models/OrderHistroy.Model.js"
import ObjectId from mongoose.Types.ObjectId;

//Fetching Orders for Seller Account by Their Status
const fetchOrdersByStatus = async(req,resp)=> {
    const {sellerId,statusCode,pageNum,limitNum} = req.params;

    if(!sellerId){
        return resp.status(404).json({"message":"Seller Not Found"});
    }

    if(!pageNum && !limitNum)
    {
       return resp.status(404).json({"message":"Need Required Paramters"})
    }

    const limit = parseInt(limitNum);

    const skip = (pageNum-1)*limit;
 
    const findOrders = await orderModel.aggregate([
        {
            $match:{sellerId:new ObjectId(sellerId),status:statusCode}
        },
        {
          $sort:{createdAt:-1}
        },
        {
           $skip:skip
        },
        {
           $limit:limit
        }       
    ]);
    
    if(!findOrders)
    {
        return resp.status(400).json({"message":"Either Orders is Empty or Internal Error"})
    }

    return resp.status(200).json(findOrders);
}

//Updating Orders Status when Order is Processing
const updateOrderStatus = async(req,resp) => {

    const {orderId,statusCode} = req.body;

    if(!orderId)
    {
        return resp.status(404).json({"message":"Order ID is Missing"});
    }

    if(!statusCode)
    {
        return resp.status(404).json({"message":"Status Code is Missing"});
    }

    const findOrderAndUpdateStatus = await orderModel.findByIdAndUpdate(orderId,{
        $set:{status:statusCode}
    })

    if(!findOrderAndUpdateStatus)
    {
        return resp.status(400).json({"message":"Something Went Wrong While Updating"})
    }

    return resp.status(200).json({"message":"Status Updated"})

}

//Showing Overview to Seller Like Revenue,CompletedOrders and Pending Orders
const pannelOrdersAndRevenueManaging = async (req, resp) => {
    const { sellerId } = req.params;

    if (!sellerId) {
        return resp.status(404).json({ "message": "Seller Not Found" });
    }

    const fetchDetails = await orderModel.aggregate([
        {
            $match: { sellerId: new ObjectId(sellerId) }
        },
        {
            $group: {
                _id: {  
                    $cond: [
                        { $eq: ["$status", "Delivered"] }, "Delivered",
                        { $cond: [
                            { $in: ["$status", ["Pending", "Preparing", "Out-For-Delivery"]] }, "Others",
                            null
                        ]}
                    ]
                },
                count: { $sum: 1 },
                revenue: { 
                    $sum: { 
                        $cond: [{ $eq: ["$status", "Delivered"] }, "$price", 0]  
                    } 
                }
            }
        },
        {
            $match: { _id: { $ne: null } }  
        },
        {
            $group: {
                _id: null, 
                delivered: { $sum: { $cond: [{ $eq: ["$_id", "Delivered"] }, "$count", 0] } },
                others: { $sum: { $cond: [{ $eq: ["$_id", "Others"] }, "$count", 0] } },
                revenue: { $sum: { $cond: [{ $eq: ["$_id", "Delivered"] }, "$revenue", 0] } }  
            }
        },
        {
            $project: {
                _id: 0, 
                delivered: 1,
                others: 1,
                revenue: 1  
            }
        }
    ]);

    if (fetchDetails.length === 0) {  
        return resp.status(400).json({ "message": "No Data Found" });
    }

    return resp.status(200).json(fetchDetails[0]); 
};

export {pannelOrdersAndRevenueManaging,fetchOrdersByStatus,updateOrderStatus}