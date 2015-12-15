package edu.nure.performers;

import edu.nure.Manager;
import edu.nure.bl.constraints.ValidationException;
import edu.nure.db.dao.AbstractDAOFactory;
import edu.nure.db.dao.interfaces.ImageDAO;
import edu.nure.db.primarykey.IntegerPrimaryKey;
import edu.nure.exceptions.DBException;
import edu.nure.exceptions.InsertException;
import edu.nure.exceptions.PerformException;
import edu.nure.exceptions.SelectException;
import edu.nure.util.Action;
import edu.nure.util.ResponseBuilder;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ImagePerformer extends AbstractPerformer {
    private DiskFileItemFactory factory;
    private ImageDAO dao;

    public ImagePerformer(ResponseBuilder builder, DiskFileItemFactory factory) throws DBException {
        super(builder);
        this.factory = factory;
        dao = AbstractDAOFactory.getDAO(AbstractDAOFactory.MYSQL).getImageDAO();
    }

    public void perform() throws PerformException, IOException, DBException {
        int action = builder.getAction();

        switch (action) {
            case Action.GET_IMAGE:
                doGet();
                break;
            case Action.DELETE_IMAGE:
                doDelete();
                break;
            case Action.INSERT_IMAGE:
                doInsert();
                break;
            default:
                builder.setStatus(ResponseBuilder.STATUS_PARAM_ERROR);

        }
    }

    @Override
    protected void doGet() throws PerformException, IOException {
        try {
            if (builder.getParameter("hash") != null) {
                String hash = builder.getParameter("hash");
                if (hash.matches("[0-9A-Fa-f]+")) {
                    getLookLikes(hash);
                }
                builder.setContentType(ResponseBuilder.XML_TYPE);
                return;
            }
            if (builder.getParameter("obj") != null) {
                try {
                    int id = builder.getIntParameter("id");
                    edu.nure.bl.Image im = dao.select(new IntegerPrimaryKey(id));
                    builder.add(im);
                } catch (NumberFormatException ex) {
                    throw new PerformException("Невозможно преобразовать id в число");
                }
                builder.setContentType(ResponseBuilder.XML_TYPE);
                return;
            }

            if (builder.getParameter("full") != null) {
                builder.add(getFull(builder.getIntParameter("id")));
                builder.setContentType(ResponseBuilder.IMAGE_TYPE);
                return;
            }
            if (builder.getParameter("preview") != null) {
                builder.add(preview(getFull(builder.getIntParameter("id"))));
                builder.setContentType(ResponseBuilder.IMAGE_TYPE);
                return;
            }
            try {
                int albumId = builder.getIntParameter("albumId");
                getAllImages(albumId);
                builder.setContentType(ResponseBuilder.XML_TYPE);
            } catch (NumberFormatException ex) {
                throw new ValidationException();
            }


        } catch (ValidationException e) {
            throw new PerformException("Неверный фомат данных");
        } catch (SelectException ex) {
            Manager.setLog(ex);
            throw new PerformException("Ошибка во время работы с базой данных");
        }
    }

    private void getLookLikes(String pHash) throws SelectException {
        int li = builder.getIntParameter("limit");

        for (edu.nure.bl.Image im : dao.getLike(pHash, li)) {
            builder.add(im);
        }
    }

    private void getAllImages(int albumId) throws SelectException {
        for (edu.nure.bl.Image image : dao.getInAlbum(albumId)) {
            builder.add(image);
        }
    }

    private byte[] getFull(int id) throws SelectException {
        return dao.select(new IntegerPrimaryKey(id)).getImage();
    }

    @Override
    public void doInsert() throws PerformException, IOException {
        try {
            if (ServletFileUpload.isMultipartContent(builder.getRequest())) {
                edu.nure.bl.Image image = parseRequest(new ServletFileUpload(factory));
                image = dao.insert(image);

                if (image != null) {
                    builder.add(image);
                    builder.setStatus(ResponseBuilder.STATUS_OK);
                } else {
                    builder.setStatus(ResponseBuilder.STATUS_ERROR_WRITE);
                    builder.setText("Неудалось добавить изображение");
                }
            } else {
                throw new PerformException("Неверный формат входного пакета");
            }
        } catch (FileUploadException ex) {
            Manager.setLog(ex);
            throw new PerformException("Ошибка загрузки файла");
        } catch (InsertException ex) {
            Manager.setLog(ex);
            builder.setStatus(ResponseBuilder.STATUS_ERROR_WRITE);
            builder.setText(ex.getMessage());
        } catch (DBException ex) {
            Manager.setLog(ex);
            throw new PerformException("Ошибка обработки запроса");
        }

    }

    private edu.nure.bl.Image parseRequest(ServletFileUpload upload) throws FileUploadException, IOException {
        List items = upload.parseRequest(builder.getRequest());
        HashMap<String, String> parameters = new HashMap<>();
        byte[] buffer = null;
        for (Object it : items) {
            FileItem item = (FileItem) it;
            if (item.isFormField())
                parameters.put(item.getFieldName(), item.getString("utf-8"));
            else buffer = item.get();
        }
        return new edu.nure.bl.Image(
                parameters.get("hash"),
                edu.nure.bl.Image.ID_NOT_SET,
                buffer,
                Integer.valueOf(parameters.get("album")),
                new Date()
        );
    }

    @Override
    protected void doDelete() throws PerformException, IOException {
        try {
            int id = builder.getIntParameter("id");
            if (dao.delete("IMAGE", new IntegerPrimaryKey(id))) {
                builder.setStatus(ResponseBuilder.STATUS_OK);
            } else {
                builder.setStatus(ResponseBuilder.STATUS_ERROR_WRITE);
                builder.setText("Неудалось удалить изображение");
            }
        } catch (DBException e) {
            Manager.setLog(e);
            throw new PerformException("Ошибка обработки запроса");
        }
    }

    public byte[] preview(byte[] imgBytes) throws IOException {

        Image img = ImageIO.read(new ByteArrayInputStream(imgBytes));
        int width = img.getWidth(null);
        int height = img.getHeight(null);
        double scale = 75.0 / (double) Math.min(width, height);
        int miniWidth = (int) (scale * (double) width);
        int miniHeight = (int) (scale * (double) height);

        AffineTransform transform = new AffineTransform(
                ((double) miniWidth) / width, 0, 0,
                ((double) miniHeight) / height, 0, 0);
        AffineTransformOp transformer = new AffineTransformOp(transform, new RenderingHints(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC));
        BufferedImage fullImage = ImageIO.read(new ByteArrayInputStream(imgBytes));
        BufferedImage miniImage = new BufferedImage(miniWidth, miniHeight, BufferedImage.TYPE_3BYTE_BGR);
        transformer.filter(fullImage, miniImage);

        if (miniHeight > miniWidth) {
            miniImage = miniImage.getSubimage(0, (miniHeight - 75) / 2, 75, 75 + (miniHeight - 75) / 2);
        } else if (miniHeight < miniWidth) {
            miniImage = miniImage.getSubimage((miniWidth - 75) / 2, 0, 75 + (miniHeight - 75) / 2, 75);
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(miniImage, "jpg", out);
        return out.toByteArray();
    }
}
