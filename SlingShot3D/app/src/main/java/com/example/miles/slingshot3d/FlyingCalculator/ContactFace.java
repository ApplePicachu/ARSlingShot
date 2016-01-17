package com.example.miles.slingshot3d.FlyingCalculator;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3d;

/**
 * Created by miles on 2015/12/29.
 */
public class ContactFace {
    public final static int CONTACT_FACE_RECT = 0;
    public final static int CONTACT_FACE_CIRCLE = 1;
    public final static int IS_NO_CONTACT = 0;
    public final static int IS_CONTACT_FRONT = 1;
    public final static int IS_CONTACT_BACK = 2;
    private final int contactFaceShape;
    private Point3d contactFaceCenter;
    private Vector3d contactFaceFront;
    private Vector3d contactFaceUp;
    private double contactFaceW;
    private double contactFaceH;
    private boolean isTwoSide;
    private Point3d contactPoint;
    private Vector3d contactVector;

    private ContactFace(int contactFaceShape, Point3d contactFaceCenter, Vector3d contactFaceFront, Vector3d contactFaceUp,
                        double contactFaceW, double contactFaceH, boolean isTwoSide) {
        this.contactFaceShape = contactFaceShape;
        this.contactFaceCenter = new Point3d(contactFaceCenter);
        this.contactFaceFront = new Vector3d(contactFaceFront);
        this.contactFaceUp = new Vector3d(contactFaceUp);
        this.contactFaceW = contactFaceW;
        this.contactFaceH = contactFaceH;
        this.isTwoSide = isTwoSide;
        this.contactPoint = new Point3d();
        this.contactVector = new Vector3d();
    }

    public ContactFace(Point3d contactFaceCenter, Vector3d contactFaceFront,
                       double contactFaceD, boolean isTwoSide) { // constructor for circle contact face
        this(CONTACT_FACE_CIRCLE, contactFaceCenter, contactFaceFront, new Vector3d(), contactFaceD, contactFaceD, isTwoSide);
    }

    public ContactFace(Point3d contactFaceCenter, Vector3d contactFaceFront, Vector3d contactFaceUp,
                       double contactFaceW, double contactFaceH, boolean isTwoSide) {
        this(CONTACT_FACE_RECT, contactFaceCenter, contactFaceFront, contactFaceUp, contactFaceW, contactFaceH, isTwoSide);
        if (round(this.contactFaceFront.dot(this.contactFaceUp), 3) != 0.0) { // not orthogonal
            Vector3d rotAxis = new Vector3d();
            rotAxis.cross(this.contactFaceFront, this.contactFaceUp);
            rotAxis.normalize();
            Matrix4d transMatrix = new Matrix4d();
            transMatrix.setRotation(new AxisAngle4d(rotAxis, Math.PI / 2.0));
            this.contactFaceUp = new Vector3d(contactFaceFront);
            transMatrix.transform(this.contactFaceUp);
        }

    }

    public boolean getContactPosAndVec(Point3d ballCenter, double diameter, Matrix4d transMatrix) {
        if (isBallContact(ballCenter, diameter, transMatrix)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isBallContact(Point3d ballCenter, double diameter, Matrix4d transMatrix) {

        Point3d contactFaceCenterTrans = new Point3d(contactFaceCenter);
        transMatrix.transform(contactFaceCenterTrans);
        Vector3d contactFaceFrontTrans = new Vector3d(contactFaceFront);
        transMatrix.transform(contactFaceFrontTrans);
        contactFaceFrontTrans.normalize();

        this.contactPoint = calcPlaneLineIntersectPoint(contactFaceFrontTrans, contactFaceCenterTrans, contactFaceFrontTrans, ballCenter);

        double ballFaceDistance = this.contactPoint.distance(ballCenter);
        if (ballFaceDistance <= diameter / 2) {
            contactVector = new Vector3d(ballCenter.x - contactPoint.x, ballCenter.y - contactPoint.y, ballCenter.z - contactPoint.z);
            contactVector.normalize();

            if (contactVector.dot(contactFaceFrontTrans) < 0 && !isTwoSide) { // is ball at back face
                return false;
            }
            switch (contactFaceShape) {
                case CONTACT_FACE_RECT:
                    Vector3d contactFaceUpTrans = new Vector3d(contactFaceUp);
                    transMatrix.transform(contactFaceUpTrans);
                    contactFaceUpTrans.normalize();
                    Vector3d contactFaceRightTrans = new Vector3d();
                    contactFaceRightTrans.cross(contactFaceUpTrans, contactVector);
                    contactFaceRightTrans.normalize();
                    Vector3d contactPointVector = new Vector3d(contactPoint.x - contactFaceCenterTrans.x,
                            contactPoint.y - contactFaceCenterTrans.y,
                            contactPoint.z - contactFaceCenterTrans.z);
                    transMatrix.getScale();
                    if (Math.abs(contactPointVector.dot(contactFaceRightTrans)) <= contactFaceW *transMatrix.getScale()/ 2 &&
                            Math.abs(contactPointVector.dot(contactFaceUpTrans)) <= contactFaceH *transMatrix.getScale()/ 2) {
                        return true;
                    }
                case CONTACT_FACE_CIRCLE:
                    if (contactPoint.distance(contactFaceCenterTrans) <= (contactFaceW / 2)) {// circle face contacted
                        return true;
                    }
                default:
                    break;
            }
        }
        return false;
    }

    private Point3d calcPlaneLineIntersectPoint(Vector3d planeVector, Point3d planePoint,
                                                Vector3d lineVector, Point3d linePoint) {
        Point3d result = new Point3d();
        if (planeVector.dot(lineVector) == 0) {
            return null;
        } else {
            double t = ((planePoint.x - linePoint.x) * planeVector.x +
                    (planePoint.y - linePoint.y) * planeVector.y +
                    (planePoint.z - linePoint.z) * planeVector.z) / lineVector.dot(planeVector);
            result.x = linePoint.x + lineVector.x * t;
            result.y = linePoint.y + lineVector.y * t;
            result.z = linePoint.z + lineVector.z * t;
        }
        return result;
    }

    public Point3d getContactPoint() {
        return contactPoint;
    }

    public Vector3d getContactVector() {
        return contactVector;
    }

    public static double round(double value, int places) {
        if (places < 0)
            throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}
